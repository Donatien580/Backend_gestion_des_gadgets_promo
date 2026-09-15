package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.DistributionRequest;
import com.entreprise.gadgets.dto.request.LigneDistributionRequest;
import com.entreprise.gadgets.dto.response.DistributionResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.DistributionMapper;
import com.entreprise.gadgets.model.*;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.EtatDistribution;
import com.entreprise.gadgets.model.enums.TypeDemande;
import com.entreprise.gadgets.model.enums.TypeDistribution;
import com.entreprise.gadgets.repository.DemandeRepository;
import com.entreprise.gadgets.repository.DistributionRepository;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.service.DistributionService;
import com.entreprise.gadgets.service.PdfGeneratorService;
import com.entreprise.gadgets.service.StockService;
import com.entreprise.gadgets.service.UtilisateurCourantService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DistributionServiceImpl implements DistributionService {

    private static final int LIMITE_SUGGESTIONS = 8;

    private final DistributionRepository distributionRepository;
    private final DemandeRepository demandeRepository;
    private final GadgetRepository gadgetRepository;
    private final DistributionMapper distributionMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final StockService stockService;
    private final UtilisateurCourantService utilisateurCourantService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DistributionResponse> lister(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDistribution").descending());
        Page<Distribution> distributionsPage = distributionRepository.findAll(pageable);
        return PageResponse.from(distributionsPage.map(distributionMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public DistributionResponse obtenir(Integer id) {
        return distributionMapper.toResponse(getDistribution(id));
    }

    @Override
    public DistributionResponse creer(DistributionRequest requete) {
        Demande demande = null;
        TypeDistribution type;

        if (requete.idDemande() != null) {
            demande = demandeRepository.findById(requete.idDemande())
                    .orElseThrow(() -> RessourceIntrouvableException.pour("Demande", requete.idDemande()));

            if (demande.getEtat() != EtatDemande.AFFECTEE) {
                throw new BusinessException(
                    "La demande doit avoir été affectée par le Chef Département avant d'être distribuée (état actuel : "
                        + demande.getEtat() + ").");
            }
            if (distributionRepository.findByDemande_IdDemande(demande.getIdDemande()).isPresent()) {
                throw new BusinessException("Une distribution existe déjà pour cette demande.");
            }
            type = demande.getTypeDemande() == TypeDemande.INTERNE ? TypeDistribution.INTERNE : TypeDistribution.EXTERNE;
        } else {
            // Dotation : aucune demande associée, le type doit être précisé explicitement
            if (requete.typeDistribution() == null) {
                throw new BusinessException("Le type de distribution est obligatoire pour une dotation.");
            }
            type = requete.typeDistribution();
        }

        if (type == TypeDistribution.INTERNE) {
            if (requete.nomReceptionnaire() == null || requete.nomReceptionnaire().isBlank()) {
                throw new BusinessException("Le nom du réceptionnaire est obligatoire pour une distribution interne.");
            }
        } else if (requete.destinataire() == null || requete.destinataire().isBlank()) {
            throw new BusinessException("Le destinataire est obligatoire pour une distribution externe.");
        }

        Distribution distribution = Distribution.builder()
            .numeroBordereau(genererNumero())
            .demande(demande)
            .estDotation(demande == null)
            .typeDistribution(type)
            .motif(requete.motif() != null ? requete.motif() : (demande != null ? demande.getObjet() : null))
            .destinataire(requete.destinataire())
            .matriculeReceptionnaire(requete.matriculeReceptionnaire())
            .nomReceptionnaire(requete.nomReceptionnaire())
            .prenomReceptionnaire(requete.prenomReceptionnaire())
            .serviceReceptionnaire(requete.serviceReceptionnaire())
            .nombrePersonnes(requete.nombrePersonnes())
            .dateDistribution(requete.dateDistribution() != null ? requete.dateDistribution() : LocalDateTime.now())
            .etat(EtatDistribution.EN_ATTENTE)
            .build();

        for (LigneDistributionRequest ligneRequete : requete.lignes()) {
            Gadget gadget = gadgetRepository.findById(ligneRequete.idGadget())
                    .orElseThrow(() -> RessourceIntrouvableException.pour("Gadget", ligneRequete.idGadget()));

            if (ligneRequete.quantiteDistribuee() > gadget.getQuantiteDisponible()) {
                throw new BusinessException(
                    "Stock insuffisant pour \"" + gadget.getLibelle() + "\" (disponible : "
                        + gadget.getQuantiteDisponible() + ", demandé : " + ligneRequete.quantiteDistribuee() + ").");
            }

            LigneDistribution ligne = LigneDistribution.builder()
                    .gadget(gadget)
                    .quantiteDistribuee(ligneRequete.quantiteDistribuee())
                    .build();
            distribution.ajouterLigne(ligne);
        }

        Distribution saved = distributionRepository.save(distribution);
        return distributionMapper.toResponse(saved);
    }

    @Override
    public DistributionResponse executer(Integer id) {
        Distribution distribution = getDistribution(id);
        if (distribution.getEtat() != EtatDistribution.EN_ATTENTE) {
            throw new BusinessException("La distribution n'est pas en attente (état actuel : " + distribution.getEtat() + ").");
        }

        Utilisateur utilisateur = utilisateurCourantService.obtenirUtilisateurConnecte();

        for (LigneDistribution ligne : distribution.getLignes()) {
            stockService.enregistrerSortie(
                ligne.getGadget(),
                ligne.getQuantiteDistribuee(),
                utilisateur,
                "Distribution " + distribution.getNumeroBordereau(),
                distribution.getIdDistribution(),
                "DISTRIBUTION"
            );
        }

        distribution.setEtat(EtatDistribution.EXECUTEE);
        distribution.setDateDistribution(LocalDateTime.now());

        // La demande associée (s'il y en a une) est maintenant honorée
        Demande demande = distribution.getDemande();
        if (demande != null) {
            demande.setEtat(EtatDemande.TRAITEE);
            demande.setDateTraitement(LocalDateTime.now());
        }

        Distribution saved = distributionRepository.save(distribution);
        return distributionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] genererBordereauPdf(Integer id) {
        Distribution distribution = getDistribution(id);
        return pdfGeneratorService.genererBordereauPDF(distribution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggererNomsReceptionnaire(String prefixe) {
        return distributionRepository.suggererNomsReceptionnaire(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggererPrenomsReceptionnaire(String prefixe) {
        return distributionRepository.suggererPrenomsReceptionnaire(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggererServicesReceptionnaire(String prefixe) {
        return distributionRepository.suggererServicesReceptionnaire(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggererDestinataires(String prefixe) {
        return distributionRepository.suggererDestinataires(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    }

    // ================== Méthodes privées ==================

    private Distribution getDistribution(Integer id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> RessourceIntrouvableException.pour("Distribution", id));
    }

    private String genererNumero() {
        int annee = Year.now().getValue();
        long count = distributionRepository.count() + 1;
        String numero;
        do {
            numero = String.format("BORD-%d-%04d", annee, count);
            count++;
        } while (distributionRepository.existsByNumeroBordereau(numero));
        return numero;
    }
}