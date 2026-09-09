package com.entreprise.gadgets.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entreprise.gadgets.dto.request.InventaireRequest;
import com.entreprise.gadgets.dto.request.JustificationRequest;
import com.entreprise.gadgets.dto.request.LigneInventaireRequest;
import com.entreprise.gadgets.dto.response.InventaireResponse;
import com.entreprise.gadgets.exception.EcartNonJustifieException;
import com.entreprise.gadgets.exception.EtatInvalideException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.InventaireMapper;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.Inventaire;
import com.entreprise.gadgets.model.LigneInventaire;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.EtatInventaire;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.repository.InventaireRepository;
import com.entreprise.gadgets.repository.LigneInventaireRepository;
import com.entreprise.gadgets.repository.UtilisateurRepository;
import com.entreprise.gadgets.service.InventaireService;
import com.entreprise.gadgets.service.StockService;
import com.entreprise.gadgets.service.UtilisateurCourantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventaireServiceImpl implements InventaireService {
   
	private static final String TYPE_REFERENCE = "INVENTAIRE";
	//private static final final String EMAIL_UTILISATEUR_SYSTEME ="systeme@dm";
	
	private final InventaireRepository inventaireRepository;
    private final LigneInventaireRepository ligneInventaireRepository;
    private final GadgetRepository gadgetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final StockService stockService;
    private final InventaireMapper inventaireMapper;
    
    @Override
    @Transactional(readOnly= true)
    public Page<InventaireResponse> lister(Pageable pageable) {
       return inventaireRepository.findAll(pageable).map(inventaireMapper::versReponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InventaireResponse obtenirParId(Integer idInventaire) {
        return inventaireMapper.versReponse(recupererAvecLignes(idInventaire));
    }
    
    @Override
    public InventaireResponse creer(InventaireRequest requete) {
    	Inventaire inventaire = Inventaire.builder()
    			.dateInventaire(requete.dateInventaire())
    			.typeInventaire(requete.typeInventaire())
    			.observations(requete.observations())
    			.etat(EtatInventaire.EN_COURS)
    			.build();
    	return inventaireMapper.versReponse(inventaireRepository.save(inventaire));
    }
    
    @Override
    public InventaireResponse saisirLigne(Integer idInventaire, LigneInventaireRequest requete) {
        Inventaire inventaire = recupererAvecLignes(idInventaire);
        exigerEtat(inventaire, EtatInventaire.EN_COURS,
            "Impossible de saisir un résultat : l'inventaire n'est plus en cours.");

        Gadget gadget = gadgetRepository.findById(requete.idGadget())
            .orElseThrow(() -> new RessourceIntrouvableException("Gadget non trouvé"));

        LigneInventaire ligne = ligneInventaireRepository
            .findByInventaire_IdInventaireAndGadget_IdGadget(idInventaire, requete.idGadget())
            .orElseGet(() -> {
                LigneInventaire nouvelle = LigneInventaire.builder().gadget(gadget).build();
                inventaire.ajouterLigne(nouvelle);
                return nouvelle;
            });

        // Stock théorique = snapshot du stock système au moment du comptage.
        ligne.setStockTheorique(gadget.getQuantiteDisponible());
        ligne.setStockReel(requete.stockReel());
        ligne.calculerEcart();

        // Un nouveau comptage invalide une éventuelle justification déjà saisie.
        if (!ligne.necessiteJustification()) {
            ligne.setJustification(null);
        }
        ligne.setValidationJustif(false);

        ligneInventaireRepository.save(ligne);

        return inventaireMapper.versReponse(recupererAvecLignes(idInventaire));
    }

    @Override
    public InventaireResponse justifierEcart(Integer idInventaire, Integer idLigne, JustificationRequest requete) {
        Inventaire inventaire = recupererAvecLignes(idInventaire);
        
        if (inventaire.getEtat() == EtatInventaire.VALIDE) {
        	throw new EtatInvalideException("Impossible de justifier un écart : l'inventaire est déjà validé");
        }
        /*exigerEtat(inventaire, EtatInventaire.EN_COURS,
            "Impossible de justifier un écart : l'inventaire n'est plus en cours.");
         */

        LigneInventaire ligne = ligneInventaireRepository.findById(idLigne)
            .filter(l -> l.getInventaire().getIdInventaire().equals(idInventaire))
            .orElseThrow(() -> new RessourceIntrouvableException("Ligne d'inventaire non trouvée"));

        if (!ligne.necessiteJustification()) {
            throw new EcartNonJustifieException(
                "Cette ligne n'a pas d'écart : aucune justification n'est nécessaire.");
        }

        ligne.setJustification(requete.justification());
        ligneInventaireRepository.save(ligne);

        return inventaireMapper.versReponse(recupererAvecLignes(idInventaire));
    }

    @Override
    public InventaireResponse terminer(Integer idInventaire) {
        Inventaire inventaire = recupererAvecLignes(idInventaire);
        exigerEtat(inventaire, EtatInventaire.EN_COURS,
            "Seul un inventaire en cours peut être marqué comme terminé.");

        if (inventaire.getLignes().isEmpty()) {
            throw new EtatInvalideException(
                "Impossible de terminer un inventaire sans aucune ligne saisie.");
        }

        inventaire.setEtat(EtatInventaire.TERMINE);
        return inventaireMapper.versReponse(inventaireRepository.save(inventaire));
    }

    @Override
    public InventaireResponse valider(Integer idInventaire) {
        Inventaire inventaire = recupererAvecLignes(idInventaire);
        
        exigerEtat(inventaire, EtatInventaire.TERMINE,
            "Seul un inventaire terminé peut être validé.");

        //Tout écart doit être justifié avant validation.
        for (LigneInventaire ligne : inventaire.getLignes()) {
            if (ligne.necessiteJustification()
                && (ligne.getJustification() == null || ligne.getJustification().isBlank())) {
                throw new EcartNonJustifieException(
                    "L'écart sur \"" + ligne.getGadget().getLibelle()
                        + "\" doit être justifié avant validation de l'inventaire.");
            }
        }

        //Mise à jour automatique du stock pour chaque écart constaté.
        Utilisateur utilisateur = getUtilisateurTemporaire();
        for (LigneInventaire ligne : inventaire.getLignes()) {
            if (ligne.necessiteJustification()) {
                stockService.enregistrerAjustementInventaire(
                    ligne.getGadget(),
                    ligne.getEcart(),
                    utilisateur,
                    //inventaire.getRealisateur(),
                    "Ajustement d'inventaire #" + idInventaire + " - " + ligne.getJustification(),
                    idInventaire,
                    TYPE_REFERENCE
                );
                ligne.setValidationJustif(true);
            }
        }

        inventaire.setEtat(EtatInventaire.VALIDE);
        return inventaireMapper.versReponse(inventaireRepository.save(inventaire));
    }

    private Inventaire recupererAvecLignes(Integer idInventaire) {
        return inventaireRepository.findWithLignesByIdInventaire(idInventaire)
            .orElseThrow(() -> new RessourceIntrouvableException("Inventaire non trouvé"));
    }

    private void exigerEtat(Inventaire inventaire, EtatInventaire attendu, String message) {
        if (inventaire.getEtat() != attendu) {
            throw new EtatInvalideException(message);
        }
    }
    
    private Utilisateur getUtilisateurTemporaire() {
    	return utilisateurRepository.findById(1)
    			.orElseThrow(()-> new RessourceIntrouvableException("Utilisateur par défaut introuvable"));
    }
}
