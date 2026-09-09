package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.InventaireRequest;
import com.entreprise.gadgets.dto.request.JustificationRequest;
import com.entreprise.gadgets.dto.request.LigneInventaireRequest;
import com.entreprise.gadgets.dto.response.InventaireResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventaireService {

    Page<InventaireResponse> lister(Pageable pageable);

    InventaireResponse obtenirParId(Integer idInventaire);

    InventaireResponse creer(InventaireRequest requete);

    InventaireResponse saisirLigne(Integer idInventaire, LigneInventaireRequest requete);

    InventaireResponse justifierEcart(Integer idInventaire, Integer idLigne, JustificationRequest requete);

    InventaireResponse terminer(Integer idInventaire);

    InventaireResponse valider(Integer idInventaire);
}