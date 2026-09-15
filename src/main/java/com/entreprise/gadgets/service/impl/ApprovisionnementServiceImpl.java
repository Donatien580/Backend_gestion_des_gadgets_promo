package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.ApprovisionnementRequest;
import com.entreprise.gadgets.dto.request.CorrectionApprovisionnementRequest;
import com.entreprise.gadgets.dto.request.LigneApprovisionnementRequest;
import com.entreprise.gadgets.dto.request.LigneCorrectionRequest;
import com.entreprise.gadgets.dto.response.ApprovisionnementResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.ApprovisionnementMapper;
import com.entreprise.gadgets.model.Approvisionnement;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.Incident;
import com.entreprise.gadgets.model.LigneApprovisionnement;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.StatutApprovisionnement;
import com.entreprise.gadgets.model.enums.StatutIncident;
import com.entreprise.gadgets.model.enums.TypeIncident;
import com.entreprise.gadgets.repository.ApprovisionnementRepository;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.service.ApprovisionnementService;
import com.entreprise.gadgets.service.StockService;
import com.entreprise.gadgets.service.UtilisateurCourantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovisionnementServiceImpl implements ApprovisionnementService {

	private final ApprovisionnementRepository approvisionnementRepository;
    private final GadgetRepository gadgetRepository;
    private final StockService stockService;
    private final UtilisateurCourantService utilisateurCourantService;
    private final ApprovisionnementMapper approvisionnementMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovisionnementResponse> lister(Pageable pageable, String recherche) {
        String rechercheNettoyee = (recherche == null || recherche.isBlank()) ? null : recherche.trim();
        return approvisionnementRepository.rechercherParFournisseur(rechercheNettoyee, pageable)
            .map(approvisionnementMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovisionnementResponse obtenirParId(Integer id) {
        return approvisionnementMapper.toResponse(trouverParId(id));
    }

    @Override
    @Transactional
    public ApprovisionnementResponse creer(ApprovisionnementRequest requete) {
        Utilisateur utilisateur = utilisateurCourantService.obtenirUtilisateurConnecte();

        Approvisionnement approvisionnement = Approvisionnement.builder()
            .dateReception(requete.dateReception() != null ? requete.dateReception() : LocalDateTime.now())
            .fournisseur(requete.fournisseur())
            .adresseFournisseur(requete.adresseFournisseur())
            .numeroPV(requete.numeroPV())
            .numeroMarche(requete.numeroMarche())
            .observations(requete.observations())
            .build();
        approvisionnement = approvisionnementRepository.save(approvisionnement);

        for (LigneApprovisionnementRequest ligneRequete : requete.lignes()) {
            traiterLigne(approvisionnement, ligneRequete, utilisateur);
        }

        return approvisionnementMapper.toResponse(approvisionnement);
    }

    @Override
    @Transactional
    public ApprovisionnementResponse corriger(Integer idApprovisionnement, CorrectionApprovisionnementRequest requete) {
        Utilisateur utilisateur = utilisateurCourantService.obtenirUtilisateurConnecte();
        Approvisionnement approvisionnement = trouverParId(idApprovisionnement);

        for (LigneCorrectionRequest ligneRequete : requete.lignes()) {
            corrigerLigne(approvisionnement, ligneRequete, utilisateur);
        }

        approvisionnement.setStatut(StatutApprovisionnement.CORRIGE);

        return approvisionnementMapper.toResponse(approvisionnement);
    }

    /**
     * Crée la ligne, augmente le stock (quantité conforme uniquement) et
     * ouvre un incident qualité si des exemplaires défectueux sont signalés.
     */
    private void traiterLigne(Approvisionnement approvisionnement, LigneApprovisionnementRequest ligneRequete,
        Utilisateur utilisateur) {

        Gadget gadget = gadgetRepository.findById(ligneRequete.idGadget())
            .orElseThrow(() -> RessourceIntrouvableException.pour("Gadget", ligneRequete.idGadget()));

        int quantiteRecue = ligneRequete.quantiteRecue();
        int quantiteDefectueuse = ligneRequete.quantiteDefectueuse() != null ? ligneRequete.quantiteDefectueuse() : 0;

        validerQuantites(quantiteRecue, quantiteDefectueuse, gadget.getLibelle());

        int quantiteConforme = quantiteRecue - quantiteDefectueuse;

        LigneApprovisionnement ligne = LigneApprovisionnement.builder()
            .approvisionnement(approvisionnement)
            .gadget(gadget)
            .quantiteCommandee(ligneRequete.quantiteCommandee())
            .quantiteRecue(quantiteRecue)
            .quantiteDefectueuse(quantiteDefectueuse)
            .observationQualite(ligneRequete.observationQualite())
            .build();
        approvisionnement.getLignes().add(ligne);

        if (quantiteConforme > 0) {
            stockService.enregistrerEntree(
                gadget, quantiteConforme, utilisateur,
                "Approvisionnement fournisseur : " + approvisionnement.getFournisseur(),
                approvisionnement.getIdApprovisionnement(), "APPROVISIONNEMENT");
        }

        if (quantiteDefectueuse > 0) {
            Incident incident = Incident.builder()
                .approvisionnement(approvisionnement)
                .typeIncident(TypeIncident.MAUVAIS_ETAT)
                .statut(StatutIncident.EN_COURS)
                .description(quantiteDefectueuse + " exemplaire(s) défectueux signalé(s) sur \""
                    + gadget.getLibelle() + "\" lors de la réception"
                    + (ligneRequete.observationQualite() != null ? " : " + ligneRequete.observationQualite() : "."))
                .build();
            approvisionnement.getIncidents().add(incident);
        }
    }

    /**
     * Désactive la ligne erronée et la remplace par une nouvelle ligne active ;
     * seul l'écart de quantité conforme est répercuté sur le stock du gadget.
     */
    private void corrigerLigne(Approvisionnement approvisionnement, LigneCorrectionRequest ligneRequete,
        Utilisateur utilisateur) {

        LigneApprovisionnement ligneErronee = approvisionnement.getLignes().stream()
            .filter(l -> l.getIdLigne().equals(ligneRequete.idLigne()) && Boolean.TRUE.equals(l.getActif()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(
                "Aucune ligne active portant l'identifiant " + ligneRequete.idLigne()
                    + " n'a été trouvée pour cet approvisionnement (déjà corrigée ou inexistante)."));

        Gadget gadget = ligneErronee.getGadget();

        int nouvelleQuantiteRecue = ligneRequete.quantiteRecue();
        int nouvelleQuantiteDefectueuse = ligneRequete.quantiteDefectueuse() != null ? ligneRequete.quantiteDefectueuse() : 0;
        validerQuantites(nouvelleQuantiteRecue, nouvelleQuantiteDefectueuse, gadget.getLibelle());

        int ancienneQuantiteDefectueuse = ligneErronee.getQuantiteDefectueuse() != null ? ligneErronee.getQuantiteDefectueuse() : 0;
        int ancienneQuantiteConforme = ligneErronee.getQuantiteRecue() - ancienneQuantiteDefectueuse;
        int nouvelleQuantiteConforme = nouvelleQuantiteRecue - nouvelleQuantiteDefectueuse;

        // 1. La ligne erronée est désactivée, jamais supprimée : trace conservée
        ligneErronee.setActif(false);
        ligneErronee.setMotifCorrection(ligneRequete.motifCorrection());

        // 2. Nouvelle ligne active qui remplace la précédente
        LigneApprovisionnement ligneCorrigee = LigneApprovisionnement.builder()
            .approvisionnement(approvisionnement)
            .gadget(gadget)
            .quantiteCommandee(ligneRequete.quantiteCommandee() != null
                ? ligneRequete.quantiteCommandee() : ligneErronee.getQuantiteCommandee())
            .quantiteRecue(nouvelleQuantiteRecue)
            .quantiteDefectueuse(nouvelleQuantiteDefectueuse)
            .observationQualite(ligneRequete.observationQualite())
            .actif(true)
            .ligneRemplacee(ligneErronee)
            .build();
        approvisionnement.getLignes().add(ligneCorrigee);

        // 3. Seul l'écart de quantité conforme est répercuté sur le stock
        int delta = nouvelleQuantiteConforme - ancienneQuantiteConforme;
        stockService.enregistrerCorrection(gadget, delta, utilisateur,
            "Correction de saisie sur l'approvisionnement de \"" + approvisionnement.getFournisseur()
                + "\" : " + ligneRequete.motifCorrection(),
            approvisionnement.getIdApprovisionnement(), "APPROVISIONNEMENT");
    }

    private void validerQuantites(int quantiteRecue, int quantiteDefectueuse, String libelleGadget) {
        if (quantiteDefectueuse > quantiteRecue || quantiteDefectueuse < 0) {
            throw new BusinessException(
                "La quantité défectueuse (" + quantiteDefectueuse + ") est un nombre entier positif et ne peut pas dépasser la quantité reçue ("
                    + quantiteRecue + ") pour \"" + libelleGadget + "\".");
        }
    }

    private Approvisionnement trouverParId(Integer id) {
        return approvisionnementRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Approvisionnement", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> suggererFournisseurs(String prefixe) {
        return approvisionnementRepository.suggererFournisseurs(prefixe, PageRequest.of(0, 8));
    }
}
