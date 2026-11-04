package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.request.RefusDemandeRequest;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.model.enums.EtatDemande;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface DemandeService {

    PageResponse<DemandeResponse> lister(EtatDemande etat, String recherche, int page, int size);

    DemandeResponse obtenir(Integer id);

    DemandeResponse creer(DemandeRequest requete);

    /** Correction d'erreur de saisie, uniquement tant que EN_ATTENTE. */
    DemandeResponse modifier(Integer id, DemandeRequest requete);

    /**
     * Valide la demande.
     * - État EN_ATTENTE → Chef Département valide (passe à VALIDEE_CHEF_DEPARTEMENT)
     * - État VALIDEE_CHEF_DEPARTEMENT → Chef Service valide (passe à AFFECTEE)
     */
    DemandeResponse valider(Integer id);

    /**
     * Affectation directe au Chef Service (raccourci sans validation préalable).
     * Uniquement depuis EN_ATTENTE, réservé au Chef Département.
     */
    DemandeResponse affecter(Integer id);

    /**
     * Traitement final de la demande par le Chef Service.
     * Uniquement depuis AFFECTEE.
     */
    DemandeResponse traiter(Integer id);

    DemandeResponse refuser(Integer id, RefusDemandeRequest requete);

    DemandeResponse annuler(Integer id);

    DemandeResponse uploaderPieceJustificative(Integer idDemande, MultipartFile fichier);

    List<String> suggererNoms(String prefixe);
    List<String> suggererPrenoms(String prefixe);
    List<String> suggererServices(String prefixe);
    List<String> suggererStructures(String prefixe);
}