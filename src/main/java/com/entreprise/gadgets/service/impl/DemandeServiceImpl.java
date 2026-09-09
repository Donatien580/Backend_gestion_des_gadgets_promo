package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.request.LigneDemandeRequest;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.DemandeMapper;
import com.entreprise.gadgets.model.*;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.TypeDemande;
import com.entreprise.gadgets.repository.*;
import com.entreprise.gadgets.service.DemandeService;
import com.entreprise.gadgets.service.FichierStockageService;
import com.entreprise.gadgets.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DemandeServiceImpl implements DemandeService {

    private final DemandeRepository demandeRepository;
    private final GadgetRepository gadgetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final ServiceDemandeurRepository serviceRepository;
    private final StockService stockService;
    private final FichierStockageService fichierStockageService;
    private final DemandeMapper demandeMapper;

    // ---------- LISTER ----------
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DemandeResponse> lister(EtatDemande etat, String recherche, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDemande").descending());
        Page<Demande> demandesPage;

        boolean hasEtat = etat != null;
        boolean hasRecherche = recherche != null && !recherche.isBlank();

        if (hasEtat && hasRecherche) {
            demandesPage = demandeRepository.findByEtatAndRecherche(etat, recherche, pageable);
        } else if (hasEtat) {
            demandesPage = demandeRepository.findByEtat(etat, pageable);
        } else if (hasRecherche) {
            demandesPage = demandeRepository.rechercher(recherche, pageable);
        } else {
            demandesPage = demandeRepository.findAll(pageable);
        }

        Page<DemandeResponse> responsePage = demandesPage.map(demandeMapper::toResponse);
        return PageResponse.from(responsePage);
    }
    // ---------- OBTENIR ----------
    @Override
    @Transactional(readOnly = true)
    public DemandeResponse obtenir(Integer id) {
        Demande demande = getDemande(id);
        return demandeMapper.toResponse(demande);
    }

    // ---------- CRÉER ----------
    @Override
    public DemandeResponse creer(DemandeRequest requete) {
        validerRequete(requete);

        Demande demande = new Demande();
        demande.setNumeroDemande(genererNumero());
        demande.setObjet(requete.objet());
        demande.setTypeDemande(requete.typeDemande());
        demande.setDateSouhaitee(requete.dateSouhaitee());
        demande.setObservations(requete.observations());
        demande.setEtat(EtatDemande.EN_ATTENTE);
        demande.setAgentSaisie(getAgentSaisieTemporaire());

        // Champs spécifiques
        if (requete.typeDemande() == TypeDemande.INTERNE) {
            Services service = serviceRepository.findById(requete.idService())
                    .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));
            demande.setService(service);
            demande.setNombrePersonnelsImpactes(requete.nombrePersonnelsImpactes());
        } else {
            demande.setStructure(requete.structure());
            demande.setRepresentant(requete.representant());
            demande.setTelephone(requete.telephone());
        }

        // Lignes
        for (LigneDemandeRequest lr : requete.lignes()) {
            Gadget gadget = gadgetRepository.findById(lr.idGadget())
                    .orElseThrow(() -> new RessourceIntrouvableException("Gadget introuvable"));
            LigneDemande ligne = LigneDemande.builder()
                    .gadget(gadget)
                    .quantiteDemandee(lr.quantiteDemandee())
                    .build();
            demande.ajouterLigne(ligne);
        }

        Demande saved = demandeRepository.save(demande);
        return demandeMapper.toResponse(saved);
    }

    // ---------- MODIFIER ----------
    @Override
    public DemandeResponse modifier(Integer id, DemandeRequest requete) {
        Demande demande = getDemande(id);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException("La demande ne peut plus être modifiée (état actuel : " + demande.getEtat() + ")");
        }

        validerRequete(requete);

        demande.setObjet(requete.objet());
        demande.setTypeDemande(requete.typeDemande());
        demande.setDateSouhaitee(requete.dateSouhaitee());
        demande.setObservations(requete.observations());

        if (requete.typeDemande() == TypeDemande.INTERNE) {
            Services service = serviceRepository.findById(requete.idService())
                    .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));
            demande.setService(service);
            demande.setNombrePersonnelsImpactes(requete.nombrePersonnelsImpactes());
            // Nettoyer champs externes
            demande.setStructure(null);
            demande.setRepresentant(null);
            demande.setTelephone(null);
        } else {
            demande.setStructure(requete.structure());
            demande.setRepresentant(requete.representant());
            demande.setTelephone(requete.telephone());
            // Nettoyer champs internes
            demande.setService(null);
            demande.setNombrePersonnelsImpactes(null);
        }

        // Remplacement des lignes
        demande.getLignes().clear();
        for (LigneDemandeRequest lr : requete.lignes()) {
            Gadget gadget = gadgetRepository.findById(lr.idGadget())
                    .orElseThrow(() -> new RessourceIntrouvableException("Gadget introuvable"));
            LigneDemande ligne = LigneDemande.builder()
                    .gadget(gadget)
                    .quantiteDemandee(lr.quantiteDemandee())
                    .build();
            demande.ajouterLigne(ligne);
        }

        Demande saved = demandeRepository.save(demande);
        return demandeMapper.toResponse(saved);
    }

    // ---------- VALIDER ----------
    @Override
    public DemandeResponse valider(Integer id) {
        Demande demande = getDemande(id);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException("La demande ne peut pas être validée (état : " + demande.getEtat() + ")");
        }
        demande.setEtat(EtatDemande.VALIDEE_CHEF_DEPARTEMENT);
        demande.setDateValidation(LocalDateTime.now());
        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- REFUSER ----------
    @Override
    public DemandeResponse refuser(Integer id, String motif) {
        Demande demande = getDemande(id);
        if (demande.getEtat() == EtatDemande.TRAITEE || demande.getEtat() == EtatDemande.REFUSEE) {
            throw new BusinessException("La demande est déjà clôturée");
        }
        demande.setEtat(EtatDemande.REFUSEE);
        demande.setMotifRefus(motif);
        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- ANNULER ----------
    @Override
    public DemandeResponse annuler(Integer id) {
        Demande demande = getDemande(id);
        demande.setEtat(EtatDemande.ANNULEE);
        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- AFFECTER ----------
    /*@Override
    public DemandeResponse affecter(Integer id, Integer idAgent) {
        Demande demande = getDemande(id);
        if (demande.getEtat() != EtatDemande.VALIDEE_CHEF_DEPARTEMENT) {
            throw new BusinessException("La demande doit être validée par le Chef Département avant affectation");
        }
        Utilisateur agent = utilisateurRepository.findById(idAgent)
                .orElseThrow(() -> new RessourceIntrouvableException("Agent introuvable"));
        demande.setAgentAffecte(agent);
        demande.setEtat(EtatDemande.AFFECTEE);
        return demandeMapper.toResponse(demandeRepository.save(demande));
    }*/
    @Override
    public DemandeResponse affecter(Integer id) {
        Demande demande = getDemande(id);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE
                && demande.getEtat() != EtatDemande.VALIDEE_CHEF_DEPARTEMENT) {
            throw new BusinessException("La demande doit être en attente ou validée pour être affectée");
        }

        // Passer à AFFECTEE
        demande.setEtat(EtatDemande.AFFECTEE);

        Demande saved = demandeRepository.save(demande);
        return demandeMapper.toResponse(saved);
    }

    // ---------- TRAITER ----------
    @Override
    public DemandeResponse traiter(Integer id, String decision, String motifRefus) {
        Demande demande = getDemande(id);
        if (demande.getEtat() != EtatDemande.AFFECTEE) {
            throw new BusinessException("La demande doit être affectée avant traitement");
        }

        if ("ACCEPTER".equalsIgnoreCase(decision)) {
            // Vérification du stock (sans décrémenter)
            for (LigneDemande ligne : demande.getLignes()) {
                Gadget gadget = ligne.getGadget();
                if (gadget.getQuantiteDisponible() < ligne.getQuantiteDemandee()) {
                    throw new BusinessException("Stock insuffisant pour le gadget : " + gadget.getLibelle());
                }
            }
            // Marquer la demande comme traitée sans décrémenter le stock
            demande.setEtat(EtatDemande.TRAITEE);
            demande.setDateValidation(LocalDateTime.now());
        } else if ("REFUSER".equalsIgnoreCase(decision)) {
            demande.setEtat(EtatDemande.REFUSEE);
            demande.setMotifRefus(motifRefus);
        } else {
            throw new BusinessException("Décision invalide");
        }

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- UPLOAD PIÈCE ----------
    @Override
    public DemandeResponse uploaderPieceJustificative(Integer idDemande, MultipartFile fichier) {
        Demande demande = getDemande(idDemande);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException("Impossible de modifier la pièce justificative après validation");
        }

        String cheminRelatif = fichierStockageService.enregistrer(fichier, "demandes");

        PieceJustificative piece = demande.getPieceJustificative();
        if (piece == null) {
            piece = PieceJustificative.builder().demande(demande).build();
        } else {
            if (piece.getCheminFichier() != null) {
                fichierStockageService.supprimer(piece.getCheminFichier());
            }
        }

        piece.setNomFichier(fichier.getOriginalFilename());
        piece.setCheminFichier(cheminRelatif);
        piece.setTypeFichier(fichier.getContentType());
        piece.setTaille(fichier.getSize());

        demande.setPieceJustificative(piece);
        pieceJustificativeRepository.save(piece);
        demandeRepository.save(demande);

        return demandeMapper.toResponse(demande);
    }

    // ================== MÉTHODES PRIVÉES ==================

    private Demande getDemande(Integer id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Demande introuvable"));
    }

    private void validerRequete(DemandeRequest requete) {
        if (requete.typeDemande() == TypeDemande.INTERNE) {
            if (requete.idService() == null) {
                throw new BusinessException("Le service est obligatoire pour une demande interne.");
            }
            if (requete.nombrePersonnelsImpactes() == null || requete.nombrePersonnelsImpactes() <= 0) {
                throw new BusinessException("Le nombre de personnels impactés est obligatoire et doit être positif.");
            }
        } else {
            if (requete.structure() == null || requete.structure().isBlank()) {
                throw new BusinessException("La structure est obligatoire pour une demande externe.");
            }
            if (requete.representant() == null || requete.representant().isBlank()) {
                throw new BusinessException("Le représentant est obligatoire.");
            }
        }
    }

    private String genererNumero() {
        int annee = Year.now().getValue();
        long count = demandeRepository.count() + 1;
        String numero;
        do {
            numero = String.format("DEM-%d-%04d", annee, count);
            count++;
        } while (demandeRepository.existsByNumeroDemande(numero));
        return numero;
    }

    private Utilisateur getAgentSaisieTemporaire() {
        // TODO : remplacer par l'utilisateur connecté via Spring Security
        return utilisateurRepository.findById(1)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur par défaut introuvable"));
    }
}