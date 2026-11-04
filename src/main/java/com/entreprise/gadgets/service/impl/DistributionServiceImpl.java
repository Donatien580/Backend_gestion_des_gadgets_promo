package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.CorrectionDistributionRequest;
import com.entreprise.gadgets.dto.request.DistributionRequest;
import com.entreprise.gadgets.dto.request.GroupeBeneficiaireRequest;
import com.entreprise.gadgets.dto.request.LigneGroupeRequest;
import com.entreprise.gadgets.dto.response.DistributionResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.DistributionMapper;
import com.entreprise.gadgets.model.*;
import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // ================== LISTE / RECHERCHE ==================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DistributionResponse> lister(String recherche, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.DESC, "dateDistribution", "idDistribution"));

        Page<Distribution> resultat = (recherche == null || recherche.isBlank())
            ? distributionRepository.findAll(pageable)
            : distributionRepository.rechercher(recherche.trim(), pageable);

        return PageResponse.from(resultat.map(distributionMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public DistributionResponse obtenir(Integer id) {
        return distributionMapper.toResponse(getDistribution(id));
    }

    // ================== CRÉER ==================

    @Override
    @Transactional
    public DistributionResponse creer(DistributionRequest requete) {

        // Détermination de la demande et du type effectif
        Demande demande = null;
        TypeDistribution type;

        if (requete.idDemande() != null) {
            demande = demandeRepository.findById(requete.idDemande())
                .orElseThrow(() -> RessourceIntrouvableException.pour("Demande", requete.idDemande()));

            if (demande.getEtat() != EtatDemande.TRAITEE) {
                throw new BusinessException(
                    "La demande doit avoir été traitée (validée) par le Chef de Service avant d'être distribuée (état actuel : "
                        + demande.getEtat() + ").");
            }
            // Les groupes de bénéficiaires ne concernent que les dotations
            // (retirer ce contrôle si on veux les autoriser aussi pour une demande)
            if (requete.groupes() != null && requete.groupes().size() > 1) {
                throw new BusinessException("Les groupes de bénéficiaires ne s'appliquent qu'aux dotations.");
            }
            type = demande.getTypeDemande() == TypeDemande.INTERNE
                ? TypeDistribution.INTERNE
                : TypeDistribution.EXTERNE;
        } else {
            if (requete.typeDistribution() == null) {
                throw new BusinessException("Le type de distribution est obligatoire pour une dotation.");
            }
            if (requete.typeDistribution() == TypeDistribution.EXTERNE) {
                throw new BusinessException(
                    "Une distribution externe doit obligatoirement être liée à une demande. "
                    + "Une dotation est nécessairement interne.");
            }
            type = requete.typeDistribution();
        }

        // Validation des champs selon le type EFFECTIF (celui de la demande si demande liée)
        if (type == TypeDistribution.INTERNE) {
            if (requete.nomReceptionnaire() == null || requete.nomReceptionnaire().isBlank()) {
                throw new BusinessException("Le nom du réceptionnaire est obligatoire pour une distribution interne.");
            }
        } else {
            if (requete.destinataire() == null || requete.destinataire().isBlank()) {
                throw new BusinessException("Le destinataire est obligatoire pour une distribution externe.");
            }
        }

        // Groupes -> lignes calculées (validation + vérification du stock sur le cumul)
        List<LignePreparee> lignes = preparerLignes(requete.groupes());

        // Construction de la distribution
        Distribution distribution = Distribution.builder()
            .numeroBordereau(genererNumero())
            .demande(demande)
            .estDotation(demande == null)
            .typeDistribution(type)
            .motif(requete.motif() != null ? requete.motif()
                                           : (demande != null ? demande.getObjet() : null))
            .structureBeneficiaire(requete.structureBeneficiaire())
            // On ne persiste QUE les champs pertinents selon le type effectif
            .destinataire(type == TypeDistribution.EXTERNE ? requete.destinataire() : null)
            .matriculeReceptionnaire(type == TypeDistribution.INTERNE ? requete.matriculeReceptionnaire() : null)
            .nomReceptionnaire(type == TypeDistribution.INTERNE ? requete.nomReceptionnaire() : null)
            .prenomReceptionnaire(type == TypeDistribution.INTERNE ? requete.prenomReceptionnaire() : null)
            .serviceReceptionnaire(type == TypeDistribution.INTERNE ? requete.serviceReceptionnaire() : null)
            .nombrePersonnes(totalBeneficiaires(requete.groupes()))
            .dateDistribution(requete.dateDistribution() != null ? requete.dateDistribution() : LocalDateTime.now())
            .etat(EtatDistribution.EN_ATTENTE)
            .build();

        lignes.forEach(lp -> distribution.ajouterLigne(construireLigne(lp, null)));
        appliquerSignataires(distribution, requete.groupes());

        return distributionMapper.toResponse(distributionRepository.save(distribution));
    }

    // ================== EXÉCUTER ==================

    @Override
    public DistributionResponse executer(Integer id) {
        Distribution distribution = getDistribution(id);
        if (distribution.getEtat() != EtatDistribution.EN_ATTENTE) {
            throw new BusinessException(
                "La distribution n'est pas en attente (état actuel : " + distribution.getEtat() + ").");
        }

        Utilisateur utilisateur = utilisateurCourantService.obtenirUtilisateurConnecte();

        // Seules les lignes actives génèrent des mouvements de stock
        List<LigneDistribution> lignesActives = distribution.getLignes().stream()
            .filter(l -> Boolean.TRUE.equals(l.getActif()))
            .toList();

        if (lignesActives.isEmpty()) {
            throw new BusinessException("Aucune ligne active à exécuter.");
        }

        // Un seul mouvement de stock par gadget (cumul de tous les groupes)
        Map<Integer, Integer> totalParGadget = new LinkedHashMap<>();
        Map<Integer, Gadget> gadgets = new HashMap<>();
        for (LigneDistribution l : lignesActives) {
            Integer idGadget = l.getGadget().getIdGadget();
            totalParGadget.merge(idGadget, l.getQuantiteDistribuee(), Integer::sum);
            gadgets.putIfAbsent(idGadget, l.getGadget());
        }
        totalParGadget.forEach((idGadget, total) -> stockService.enregistrerSortie(
            gadgets.get(idGadget),
            total,
            utilisateur,
            "Distribution " + distribution.getNumeroBordereau(),
            distribution.getIdDistribution(),
            "DISTRIBUTION"
        ));

        distribution.setEtat(EtatDistribution.EXECUTEE);
        distribution.setDateDistribution(LocalDateTime.now());

        // La demande liée est déjà TRAITEE (validée par le Chef de Service) :
        // on n'y touche plus, pour ne pas écraser sa date de traitement.

        return distributionMapper.toResponse(distributionRepository.save(distribution));
    }

    // ================== CORRIGER ==================

    @Override
    @Transactional
    public DistributionResponse corriger(Integer id, CorrectionDistributionRequest requete) {
        Distribution distribution = getDistribution(id);

        // Interdit si la distribution a déjà été exécutée
        if (distribution.getEtat() != EtatDistribution.EN_ATTENTE) {
            throw new BusinessException(
                "Seule une distribution en attente peut être corrigée (état actuel : "
                    + distribution.getEtat() + ").");
        }

        TypeDistribution type = distribution.getTypeDistribution();

        // Validation des champs selon le type
        if (type == TypeDistribution.INTERNE) {
            if (requete.nomReceptionnaire() == null || requete.nomReceptionnaire().isBlank()) {
                throw new BusinessException("Le nom du réceptionnaire est obligatoire pour une distribution interne.");
            }
        } else {
            if (requete.destinataire() == null || requete.destinataire().isBlank()) {
                throw new BusinessException("Le destinataire est obligatoire pour une distribution externe.");
            }
        }

        if (distribution.getDemande() != null
                && requete.groupes() != null && requete.groupes().size() > 1) {
            throw new BusinessException("Les groupes de bénéficiaires ne s'appliquent qu'aux dotations.");
        }

        // Groupes -> lignes calculées (validation + vérification du stock)
        List<LignePreparee> nouvelles = preparerLignes(requete.groupes());

        // Désactive les lignes actives et conserve la trace
        List<LigneDistribution> anciennesActives = distribution.getLignes().stream()
            .filter(l -> Boolean.TRUE.equals(l.getActif()))
            .toList();
        for (LigneDistribution ancienne : anciennesActives) {
            ancienne.setActif(false);
            ancienne.setMotifCorrection(requete.motifCorrection());
        }

        // Chaînage par (gadget, catégorie) : un même gadget peut figurer dans plusieurs groupes
        Map<String, LigneDistribution> ancienneParCle = new HashMap<>();
        for (LigneDistribution ancienne : anciennesActives) {
            ancienneParCle.putIfAbsent(
                cle(ancienne.getGadget(), ancienne.getCategorieBeneficiaire()), ancienne);
        }
        for (LignePreparee lp : nouvelles) {
            distribution.ajouterLigne(
                construireLigne(lp, ancienneParCle.get(cle(lp.gadget(), lp.categorie()))));
        }

        // Mise à jour des champs variables selon le type
        if (type == TypeDistribution.EXTERNE) {
            distribution.setDestinataire(requete.destinataire());
        } else {
            distribution.setMatriculeReceptionnaire(requete.matriculeReceptionnaire());
            distribution.setNomReceptionnaire(requete.nomReceptionnaire());
            distribution.setPrenomReceptionnaire(requete.prenomReceptionnaire());
            distribution.setServiceReceptionnaire(requete.serviceReceptionnaire());
        }
        distribution.setStructureBeneficiaire(requete.structureBeneficiaire());
        distribution.setNombrePersonnes(totalBeneficiaires(requete.groupes()));
        appliquerSignataires(distribution, requete.groupes());

        if (requete.motif() != null) {
            distribution.setMotif(requete.motif());
        }
        if (requete.dateDistribution() != null) {
            distribution.setDateDistribution(requete.dateDistribution());
        }

        // L'état reste EN_ATTENTE (la distribution n'a pas encore été exécutée)
        return distributionMapper.toResponse(distributionRepository.save(distribution));
    }

    // ================== BORDEREAU PDF ==================

    @Override
    @Transactional
    public byte[] genererBordereauPdf(Integer id) {
        Distribution distribution = getDistribution(id);

        // Le PDF d'abord : s'il échoue, la date n'est pas enregistrée
        byte[] pdf = pdfGeneratorService.genererBordereauPDF(distribution);

        // Date de première génération (une réimpression ne la modifie pas)
        if (distribution.getDateGenerationBordereau() == null) {
            distribution.setDateGenerationBordereau(LocalDateTime.now());
            distributionRepository.save(distribution);
        }
        return pdf;
    }

    // ================== SUGGESTIONS ==================

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

    /** Ligne calculée à partir d'un groupe de la requête. */
    private record LignePreparee(Gadget gadget, CategorieBeneficiaire categorie,
                                 int nombre, int quantiteParPersonne) {
        int total() { return nombre * quantiteParPersonne; }
    }

    private List<LignePreparee> preparerLignes(List<GroupeBeneficiaireRequest> groupes) {
        if (groupes == null || groupes.isEmpty()) {
            throw new BusinessException("La distribution doit contenir au moins un groupe avec un gadget.");
        }
        Set<CategorieBeneficiaire> categoriesVues = EnumSet.noneOf(CategorieBeneficiaire.class);
        List<LignePreparee> lignes = new ArrayList<>();

        for (GroupeBeneficiaireRequest groupe : groupes) {
            CategorieBeneficiaire categorie = groupe.categorie();
            String nomGroupe = categorie != null ? "\"" + categorie.getLibelle() + "\"" : "de la distribution";

            if (categorie == null && groupes.size() > 1) {
                throw new BusinessException("Chaque groupe doit avoir une catégorie dès qu'il y a plusieurs groupes.");
            }
            if (categorie != null && !categoriesVues.add(categorie)) {
                throw new BusinessException("La catégorie " + nomGroupe + " est saisie plusieurs fois.");
            }
            if (groupe.lignes() == null || groupe.lignes().isEmpty()) {
                throw new BusinessException("Le groupe " + nomGroupe + " doit contenir au moins un gadget.");
            }

            int nombre = groupe.nombre() != null ? groupe.nombre() : 1;

         // Noms : un par chef (catégories de chefs) ou le nom du bénéficiaire unique
            boolean beneficiaireUnique = categorie == null && groupes.size()==1;
            List<String> noms = groupe.noms() == null ? List.of()
                : groupe.noms().stream().filter(n -> n != null && !n.isBlank()).toList();
            if (!noms.isEmpty()) {
                if (!beneficiaireUnique && (categorie == null || !categorie.isChef())) {
                    throw new BusinessException("Les noms ne se saisissent que pour les catégories de chefs.");
                }
                if (noms.size() > nombre) {
                    throw new BusinessException(beneficiaireUnique
                    		? "Un seul nom de bénéficiaire peut être saisi."
                    		:"Le groupe " + nomGroupe + " compte " + nombre
                        + " personne(s) : impossible de saisir " + noms.size() + " noms.");
                }
            }

            Set<Integer> gadgetsVus = new HashSet<>();
            for (LigneGroupeRequest l : groupe.lignes()) {
                if (!gadgetsVus.add(l.idGadget())) {
                    throw new BusinessException(
                        "Un même gadget est saisi plusieurs fois dans le groupe " + nomGroupe + ".");
                }
                Gadget gadget = gadgetRepository.findById(l.idGadget())
                    .orElseThrow(() -> RessourceIntrouvableException.pour("Gadget", l.idGadget()));
                lignes.add(new LignePreparee(gadget, categorie, nombre, l.quantiteParPersonne()));
            }
        }
        verifierStock(lignes);
        return lignes;
    }

    /** Vérifie le stock sur le total par gadget, tous groupes confondus. */
    private void verifierStock(List<LignePreparee> lignes) {
        Map<Integer, Integer> totalParGadget = new LinkedHashMap<>();
        Map<Integer, Gadget> gadgets = new HashMap<>();
        for (LignePreparee l : lignes) {
            Integer id = l.gadget().getIdGadget();
            totalParGadget.merge(id, l.total(), Integer::sum);
            gadgets.putIfAbsent(id, l.gadget());
        }
        totalParGadget.forEach((id, total) -> {
            Gadget gadget = gadgets.get(id);
            if (total > gadget.getQuantiteDisponible()) {
                throw new BusinessException(
                    "Stock insuffisant pour \"" + gadget.getLibelle() + "\" (disponible : "
                        + gadget.getQuantiteDisponible() + ", demandé : " + total + ").");
            }
        });
    }

    private LigneDistribution construireLigne(LignePreparee lp, LigneDistribution remplacee) {
        return LigneDistribution.builder()
            .gadget(lp.gadget())
            .categorieBeneficiaire(lp.categorie())
            .nombreBeneficiaires(lp.nombre())
            .quantiteParPersonne(lp.quantiteParPersonne())
            .quantiteDistribuee(lp.total())      // toujours recalculé côté serveur
            .actif(true)
            .ligneRemplacee(remplacee)
            .build();
    }

    private int totalBeneficiaires(List<GroupeBeneficiaireRequest> groupes) {
        return groupes.stream().mapToInt(g -> g.nombre() != null ? g.nombre() : 1).sum();
    }

    private String cle(Gadget gadget, CategorieBeneficiaire categorie) {
        return gadget.getIdGadget() + "|" + (categorie != null ? categorie.name() : "-");
    }

    private void appliquerSignataires(Distribution distribution, List<GroupeBeneficiaireRequest> groupes) {
        distribution.getSignataires().clear();      // orphanRemoval supprime les anciens
        int ordre = 0;
        for (GroupeBeneficiaireRequest g : groupes) {
            if (g.noms() == null) continue;
            for (String nom : g.noms()) {
                if (nom == null || nom.isBlank()) continue;
                distribution.getSignataires().add(SignataireDistribution.builder()
                    .distribution(distribution)
                    .categorieBeneficiaire(g.categorie())
                    .nomComplet(nom.trim())
                    .ordre(ordre++)
                    .build());
            }
        }
    }
}