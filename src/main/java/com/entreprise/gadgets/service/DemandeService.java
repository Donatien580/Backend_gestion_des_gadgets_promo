package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.AffectationDemandeRequest;
import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.request.RefusDemandeRequest;
import com.entreprise.gadgets.dto.response.AgentResume;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.model.enums.EtatDemande;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DemandeService {
	PageResponse<DemandeResponse> lister(EtatDemande etat, String recherche, int page, int size);

	List<String> suggererNoms(String prefixe);
	List<String> suggererPrenoms(String prefixe);
	List<String> suggererServices(String prefixe);
	List<String> suggererStructures(String prefixe);
	
    DemandeResponse obtenir(Integer id);

    DemandeResponse creer(DemandeRequest requete);

    /** Uniquement possible tant que la demande est EN_ATTENTE (erreur de saisie à corriger). */
    DemandeResponse modifier(Integer id, DemandeRequest requete);

    /** Réservé au Chef Département (ou Admin, en attendant Keycloak). */
    DemandeResponse valider(Integer id);

    /** Motif obligatoire. Réservé au Chef Département (ou Admin). */
    DemandeResponse refuser(Integer id, RefusDemandeRequest requete);

    DemandeResponse annuler(Integer id);

    /** Réservé au Chef Département (ou Admin) : choisit l'agent qui va traiter la demande. */
    DemandeResponse affecter(Integer id, AffectationDemandeRequest requete);

    /** Agents éligibles à une affectation (Chef Service / Gestionnaire de stock). */
    List<AgentResume> listerAgentsAffectables();

    DemandeResponse uploaderPieceJustificative(Integer idDemande, MultipartFile fichier);
	
    //DemandeResponse traiter(Integer id, String decision, String motifRefus);
}