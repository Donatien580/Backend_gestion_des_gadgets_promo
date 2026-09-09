package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.DistributionRequest;
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
import com.entreprise.gadgets.repository.UtilisateurRepository;
import com.entreprise.gadgets.service.DistributionService;
import com.entreprise.gadgets.service.PdfGeneratorService;
import com.entreprise.gadgets.service.StockService;

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

    private final DistributionRepository distributionRepository;
    private final DemandeRepository demandeRepository;
    private final GadgetRepository gadgetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DistributionMapper distributionMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final StockService stockService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DistributionResponse> lister(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDistribution").descending());
        Page<Distribution> distributionsPage = distributionRepository.findAll(pageable);
        Page<DistributionResponse> responsePage = distributionsPage.map(distributionMapper::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public DistributionResponse obtenir(Integer id) {
        Distribution distribution = getDistribution(id);
        return distributionMapper.toResponse(distribution);
    }
    
    @Override
    public DistributionResponse executer(Integer id) {
        Distribution distribution = getDistribution(id);
        if (distribution.getEtat() != EtatDistribution.EN_ATTENTE) {
            throw new BusinessException("La distribution n'est pas en attente");
        }

        Demande demande = distribution.getDemande();
        Utilisateur utilisateur = getUtilisateurTemporaire();

        for (LigneDistribution ligne : distribution.getLignes()) {
            // 1. Décrémenter le stock
            stockService.enregistrerSortie(
                ligne.getGadget(),
                ligne.getQuantiteDistribuee(),
                utilisateur,                    
                "Distribution " + distribution.getNumeroBordereau(),
                distribution.getIdDistribution(),
                "DISTRIBUTION"
            );

            // 2. Mettre à jour la ligne de demande
            LigneDemande ligneDemande = demande.getLignes().stream()
                    .filter(l -> l.getGadget().getIdGadget().equals(ligne.getGadget().getIdGadget()))
                    .findFirst()
                    .orElse(null);
            if (ligneDemande != null) {
                ligneDemande.setQuantiteAccordee(ligne.getQuantiteDistribuee());
            }
        }

        distribution.setEtat(EtatDistribution.EXECUTEE);
        distribution.setDateDistribution(LocalDateTime.now());
        Distribution saved = distributionRepository.save(distribution);
        return distributionMapper.toResponse(saved);
    }

    private Utilisateur getUtilisateurTemporaire() {
        // TODO : remplacer par l'utilisateur connecté
        return utilisateurRepository.findById(1)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur par défaut introuvable"));
    }

    @Override
    public DistributionResponse creer(DistributionRequest requete) {
        Demande demande = demandeRepository.findById(requete.idDemande())
                .orElseThrow(() -> new RessourceIntrouvableException("Demande introuvable"));

        // Vérifier que la demande est à l'état TRAITEE (acceptée par Chef Service)
        if (demande.getEtat() != EtatDemande.VALIDEE_CHEF_DEPARTEMENT
                && demande.getEtat() != EtatDemande.TRAITEE) {
            throw new BusinessException(
                "La demande doit être validée (VALIDEE_CHEF_DEPARTEMENT) ou déjà traitée (TRAITEE)"
            );
        }

        // Vérifier qu'il n'existe pas déjà une distribution pour cette demande
        if (distributionRepository.findByDemande_IdDemande(demande.getIdDemande()).isPresent()) {
            throw new BusinessException("Une distribution existe déjà pour cette demande");
        }

        Distribution distribution = new Distribution();
        distribution.setNumeroBordereau(genererNumero());
        distribution.setDemande(demande);
        distribution.setTypeDistribution(demande.getTypeDemande() == TypeDemande.INTERNE ? TypeDistribution.INTERNE : TypeDistribution.EXTERNE);

        // Renseigner automatiquement le destinataire selon le type
        String destinataire = construireDestinataire(demande);
        distribution.setDestinataire(requete.destinataire() != null ? requete.destinataire() : destinataire);

        distribution.setMotif(requete.motif() != null ? requete.motif() : demande.getObjet());
        distribution.setDateDistribution(requete.dateDistribution() != null ? requete.dateDistribution() : LocalDateTime.now());
        distribution.setEtat(EtatDistribution.EN_ATTENTE);

        // Copier les lignes de la demande (quantité accordée = demandée si acceptée)
        for (LigneDemande ligneDemande : demande.getLignes()) {
            int quantite = (ligneDemande.getQuantiteAccordee() != null) ? ligneDemande.getQuantiteAccordee() : ligneDemande.getQuantiteDemandee();
            Gadget gadget = ligneDemande.getGadget();
            LigneDistribution ligne = LigneDistribution.builder()
                    .gadget(gadget)
                    .quantiteDistribuee(quantite)
                    .build();
            distribution.ajouterLigne(ligne);
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
    public DistributionResponse signer(Integer id, String signePar) {
        Distribution distribution = getDistribution(id);
        if (distribution.getEtat() == EtatDistribution.SIGNEE) {
            throw new BusinessException("Le bordereau est déjà signé");
        }
        distribution.setSignePar(signePar);
        distribution.setDateSignature(LocalDateTime.now());
        distribution.setEtat(EtatDistribution.SIGNEE);
        Distribution saved = distributionRepository.save(distribution);
        return distributionMapper.toResponse(saved);
    }

    // ================== Méthodes privées ==================

    private Distribution getDistribution(Integer id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Distribution introuvable"));
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

    private String construireDestinataire(Demande demande) {
        if (demande.getTypeDemande() == TypeDemande.INTERNE) {
            // Pour une demande interne, le destinataire est le service demandeur
            Services service = demande.getService();
            if (service != null) {
                return String.format("%s - Responsable: %s (%s)",
                        service.getLibelleService(),
                        service.getNomResponsable(),
                        service.getMatriculeResponsable());
            }
            return "Service non spécifié";
        } else {
            // Demande externe
            return String.format("Structure: %s - Représentant: %s",
                    demande.getStructure(),
                    demande.getRepresentant());
        }
    }
}