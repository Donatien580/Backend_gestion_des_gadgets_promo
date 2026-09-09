package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.DistributionRequest;
import com.entreprise.gadgets.dto.response.DistributionResponse;
import com.entreprise.gadgets.dto.response.PageResponse;

import java.util.List;

public interface DistributionService {

    //List<DistributionResponse> lister();
	PageResponse<DistributionResponse> lister(int page, int size);

    DistributionResponse obtenir(Integer id);

    DistributionResponse creer(DistributionRequest requete);

    byte[] genererBordereauPdf(Integer id);

    DistributionResponse executer(Integer id);
    
    DistributionResponse signer(Integer id, String signePar);
}