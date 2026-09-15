package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.ApprovisionnementRequest;
import com.entreprise.gadgets.dto.request.CorrectionApprovisionnementRequest;
import com.entreprise.gadgets.dto.response.ApprovisionnementResponse;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovisionnementService {

    Page<ApprovisionnementResponse> lister(Pageable pageable, String recherche);

    ApprovisionnementResponse obtenirParId(Integer id);

    ApprovisionnementResponse creer(ApprovisionnementRequest requete);
    
    List<String> suggererFournisseurs(String prefixe);
    /**
     * Corrige une ou plusieurs lignes erronées d'un approvisionnement déjà enregistré.
     * Chaque ligne erronée est désactivée (jamais supprimée) et remplacée par une
     * nouvelle ligne active ; le stock est ajusté de l'écart entre l'ancienne et la
     * nouvelle quantité conforme.
     */
    ApprovisionnementResponse corriger(Integer idApprovisionnement, CorrectionApprovisionnementRequest requete);
}
