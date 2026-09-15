package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.AffectationDemandeRequest;

import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.request.RefusDemandeRequest;
import com.entreprise.gadgets.dto.response.AgentResume;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.DemandeMapper;
import com.entreprise.gadgets.model.*;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.RoleType;
import com.entreprise.gadgets.model.enums.TypeDemande;
import com.entreprise.gadgets.repository.*;
import com.entreprise.gadgets.service.DemandeService;
import com.entreprise.gadgets.service.FichierStockageService;
import com.entreprise.gadgets.service.UtilisateurCourantService;

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

	/** Rôles pouvant se voir affecter une demande par le Chef Département. */
    private static final List<RoleType> ROLES_AFFECTABLES = List.of(RoleType.CHEF_SERVICE);

    private final DemandeRepository demandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final FichierStockageService fichierStockageService;
    private final UtilisateurCourantService utilisateurCourantService;
    private final DemandeMapper demandeMapper;

    // ---------- LISTER ----------
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DemandeResponse> lister(EtatDemande etat, String recherche, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDemande").descending());
        boolean hasEtat = etat != null;
        boolean hasRecherche = recherche != null && !recherche.isBlank();

        Page<Demande> demandesPage;
        if (hasEtat && hasRecherche) {
            demandesPage = demandeRepository.findByEtatAndRecherche(etat, recherche, pageable);
        } else if (hasEtat) {
            demandesPage = demandeRepository.findByEtat(etat, pageable);
        } else if (hasRecherche) {
            demandesPage = demandeRepository.rechercher(recherche, pageable);
        } else {
            demandesPage = demandeRepository.findAll(pageable);
        }

        return PageResponse.from(demandesPage.map(demandeMapper::toResponse));
    }

    // ---------- OBTENIR ----------
    @Override
    @Transactional(readOnly = true)
    public DemandeResponse obtenir(Integer id) {
        return demandeMapper.toResponse(trouverParId(id));
    }

    // ---------- CRÉER ----------
    @Override
    public DemandeResponse creer(DemandeRequest requete) {
        validerRequete(requete);

        Utilisateur agentSaisie = utilisateurCourantService.obtenirUtilisateurConnecte();

        Demande demande = Demande.builder()
            .numeroDemande(genererNumero())
            .objet(requete.objet())
            .typeDemande(requete.typeDemande())
            .dateSouhaitee(requete.dateSouhaitee())
            .observations(requete.observations())
            .etat(EtatDemande.EN_ATTENTE)
            .agentSaisie(agentSaisie)
            .build();

        appliquerChampsDemandeur(demande, requete);

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- MODIFIER (erreur de saisie) ----------
    @Override
    public DemandeResponse modifier(Integer id, DemandeRequest requete) {
        Demande demande = trouverParId(id);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException(
                "La demande ne peut plus être modifiée (état actuel : " + demande.getEtat()
                    + "). Seule une demande en attente peut être corrigée.");
        }

        validerRequete(requete);

        demande.setObjet(requete.objet());
        demande.setTypeDemande(requete.typeDemande());
        demande.setDateSouhaitee(requete.dateSouhaitee());
        demande.setObservations(requete.observations());
        appliquerChampsDemandeur(demande, requete);

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- VALIDER (Chef Département) ----------
    @Override
    public DemandeResponse valider(Integer id) {
        verifierRoleValidation("valider");

        Demande demande = trouverParId(id);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException("La demande ne peut pas être validée (état actuel : " + demande.getEtat() + ").");
        }

        demande.setEtat(EtatDemande.VALIDEE_CHEF_DEPARTEMENT);
        demande.setDateValidation(LocalDateTime.now());

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- REFUSER (Chef Département, motif obligatoire) ----------
    @Override
    public DemandeResponse refuser(Integer id, RefusDemandeRequest requete) {
        verifierRoleValidation("refuser");

        Demande demande = trouverParId(id);
        if (demande.getEtat() == EtatDemande.TRAITEE
            || demande.getEtat() == EtatDemande.REFUSEE
            || demande.getEtat() == EtatDemande.ANNULEE) {
            throw new BusinessException("La demande est déjà clôturée (état actuel : " + demande.getEtat() + ").");
        }

        demande.setEtat(EtatDemande.REFUSEE);
        demande.setMotifRefus(requete.motifRefus());

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- ANNULER ----------
    @Override
    public DemandeResponse annuler(Integer id) {
        Demande demande = trouverParId(id);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException("Seule une demande en attente peut être annulée.");
        }
        demande.setEtat(EtatDemande.ANNULEE);
        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ---------- AFFECTER (Chef Département) ----------
    @Override
    public DemandeResponse affecter(Integer id, AffectationDemandeRequest requete) {
    	   verifierRoleAffectation();

        Demande demande = trouverParId(id);
        if (demande.getEtat() != EtatDemande.VALIDEE_CHEF_DEPARTEMENT) {
            throw new BusinessException(
                "La demande doit être validée par le Chef Département avant d'être affectée (état actuel : "
                    + demande.getEtat() + ").");
        }

        Utilisateur agent = utilisateurRepository.findById(requete.idAgentAffecte())
            .orElseThrow(() -> RessourceIntrouvableException.pour("Utilisateur", requete.idAgentAffecte()));

        if (!ROLES_AFFECTABLES.contains(agent.getRole().getNom())) {
            throw new BusinessException(
                "\"" + agent.getNom() + " " + agent.getPrenom() + "\" n'a pas un rôle pouvant être affecté à une demande.");
        }

        demande.setAgentAffecte(agent);
        demande.setEtat(EtatDemande.AFFECTEE);

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentResume> listerAgentsAffectables() {
        return utilisateurRepository.findByRole_NomInAndActifTrueOrderByNomAsc(ROLES_AFFECTABLES).stream()
            .map(u -> new AgentResume(u.getIdUtilisateur(), u.getNom(), u.getPrenom()))
            .toList();
    }

    // ---------- UPLOAD PIÈCE JUSTIFICATIVE ----------
    @Override
    public DemandeResponse uploaderPieceJustificative(Integer idDemande, MultipartFile fichier) {
        Demande demande = trouverParId(idDemande);
        if (demande.getEtat() != EtatDemande.EN_ATTENTE) {
            throw new BusinessException("Impossible de modifier la pièce justificative après validation.");
        }

        String cheminRelatif = fichierStockageService.enregistrer(fichier, "demandes");

        PieceJustificative piece = demande.getPieceJustificative();
        if (piece == null) {
            piece = PieceJustificative.builder().demande(demande).build();
        } else if (piece.getCheminFichier() != null) {
            fichierStockageService.supprimer(piece.getCheminFichier());
        }

        piece.setNomFichier(fichier.getOriginalFilename());
        piece.setCheminFichier(cheminRelatif);
        piece.setTypeFichier(fichier.getContentType());
        piece.setTaille(fichier.getSize());

        demande.setPieceJustificative(piece);
        pieceJustificativeRepository.save(piece);

        return demandeMapper.toResponse(demandeRepository.save(demande));
    }

    // ================== MÉTHODES PRIVÉES ==================

    private void appliquerChampsDemandeur(Demande demande, DemandeRequest requete) {
        demande.setNomDemandeur(requete.nomDemandeur());
        demande.setPrenomDemandeur(requete.prenomDemandeur());
        demande.setTelephoneDemandeur(requete.telephoneDemandeur());

        if (requete.typeDemande() == TypeDemande.INTERNE) {
            demande.setMatriculeDemandeur(requete.matriculeDemandeur());
            demande.setServiceDemandeur(requete.serviceDemandeur());
            demande.setStructureDemandeur(null);
        } else {
            demande.setStructureDemandeur(requete.structureDemandeur());
            demande.setMatriculeDemandeur(null);
            demande.setServiceDemandeur(null);
        }
    }

    private void validerRequete(DemandeRequest requete) {
        if (requete.typeDemande() == TypeDemande.INTERNE) {
            if (requete.matriculeDemandeur() == null || requete.matriculeDemandeur().isBlank()) {
                throw new BusinessException("Le matricule du demandeur est obligatoire pour une demande interne.");
            }
            if (requete.serviceDemandeur() == null || requete.serviceDemandeur().isBlank()) {
                throw new BusinessException("Le service du demandeur est obligatoire pour une demande interne.");
            }
        } else {
            if (requete.structureDemandeur() == null || requete.structureDemandeur().isBlank()) {
                throw new BusinessException("La structure est obligatoire pour une demande externe.");
            }
        }
    }

    private void verifierRoleValidation(String action) {
        Utilisateur utilisateur = utilisateurCourantService.obtenirUtilisateurConnecte();
        RoleType role = utilisateur.getRole().getNom();
        if (role != RoleType.CHEF_DEPARTEMENT && role != RoleType.ADMIN) {
            throw new BusinessException("Seul le Chef Département ou le Chef Service peut " + action + " une demande.");
        }
    }
    
    private void verifierRoleAffectation() {
        Utilisateur utilisateur = utilisateurCourantService.obtenirUtilisateurConnecte();
        RoleType role = utilisateur.getRole().getNom();
        if (role != RoleType.CHEF_DEPARTEMENT && role != RoleType.ADMIN) {
            throw new BusinessException("Seul le Chef Département peut affecter une demande.");
        }
    }

    private Demande trouverParId(Integer id) {
        return demandeRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Demande", id));
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
    
    	private static final int LIMITE_SUGGESTIONS = 8;

    	@Override
    	@Transactional(readOnly = true)
    	public List<String> suggererNoms(String prefixe) {
    	    return demandeRepository.suggererNoms(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    	}

    	@Override
    	@Transactional(readOnly = true)
    	public List<String> suggererPrenoms(String prefixe) {
    	    return demandeRepository.suggererPrenoms(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    	}

    	@Override
    	@Transactional(readOnly = true)
    	public List<String> suggererServices(String prefixe) {
    	    return demandeRepository.suggererServices(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    	}

    	@Override
    	@Transactional(readOnly = true)
    	public List<String> suggererStructures(String prefixe) {
    	    return demandeRepository.suggererStructures(prefixe, PageRequest.of(0, LIMITE_SUGGESTIONS));
    	}
}