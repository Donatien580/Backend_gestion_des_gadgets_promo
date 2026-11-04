package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.CorrectionDistributionRequest;
import com.entreprise.gadgets.dto.request.DistributionRequest;
import com.entreprise.gadgets.dto.response.DistributionResponse;
import com.entreprise.gadgets.dto.response.PageResponse;

import java.util.List;

public interface DistributionService {

	PageResponse<DistributionResponse> lister(String recherche, int page, int size);

    DistributionResponse obtenir(Integer id);

    DistributionResponse creer(DistributionRequest requete);

    byte[] genererBordereauPdf(Integer id);
    
    /**
     * Corrige une distribution non encore exécutée (état EN_ATTENTE).
    **/   
    DistributionResponse corriger(Integer id, CorrectionDistributionRequest requete);
    
    DistributionResponse executer(Integer id);
    
    List<String> suggererNomsReceptionnaire(String prefixe);
    List<String> suggererPrenomsReceptionnaire(String prefixe);
    List<String> suggererServicesReceptionnaire(String prefixe);
    List<String> suggererDestinataires(String prefixe);
}