package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.GadgetRequest;
import com.entreprise.gadgets.dto.response.GadgetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GadgetService {

    /**
     * @param idCategorie      filtre optionnel par catégorie (null = toutes)
     * @param inclureInactifs  false par défaut : seuls les gadgets actifs sont
     *                         retournés (vue catalogue) ; true pour la vue
     *                         d'administration qui doit aussi voir les inactifs.
     * @param recherche        filtre optionnel sur le libellé (insensible à la casse)
     */
    Page<GadgetResponse> lister(Pageable pageable, Integer idCategorie, boolean inclureInactifs, String recherche);

    List<GadgetResponse> listerSousSeuilAlerte();

    GadgetResponse obtenirParId(Integer id);

    GadgetResponse creer(GadgetRequest requete);

    GadgetResponse modifier(Integer id, GadgetRequest requete);

    GadgetResponse changerStatut(Integer id, boolean actif);

    /** Remplace la photo du gadget (supprime l'ancienne si elle existait). */
    GadgetResponse mettreAJourPhoto(Integer id, MultipartFile fichier);
}
