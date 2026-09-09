package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.model.enums.EtatDemande;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DemandeService {

	PageResponse<DemandeResponse> lister(EtatDemande etat, String recherche, int page, int size);

    DemandeResponse obtenir(Integer id);

    DemandeResponse creer(DemandeRequest requete);

    DemandeResponse modifier(Integer id, DemandeRequest requete);

    DemandeResponse valider(Integer id);

    DemandeResponse refuser(Integer id, String motif);

    DemandeResponse annuler(Integer id);

    /*DemandeResponse affecter(Integer id, Integer idAgent);*/
    DemandeResponse affecter(Integer id);

    DemandeResponse traiter(Integer id, String decision, String motifRefus);

    DemandeResponse uploaderPieceJustificative(Integer idDemande, MultipartFile fichier);
}