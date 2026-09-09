package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.model.Distribution;
import com.entreprise.gadgets.model.LigneDistribution;
import com.entreprise.gadgets.dto.response.LigneDistributionResponse;
import com.entreprise.gadgets.dto.response.DistributionResponse;

import java.util.List;

import org.springframework.stereotype.Component;

import com.entreprise.gadgets.dto.response.DemandeResponse;

@Component
public class DistributionMapper {
	
	public DistributionResponse toResponse(Distribution distribution) {
		List<LigneDistributionResponse> lignes = distribution.getLignes()
				.stream()
				.map(this::toLigneResponse)
				.toList();
		
		return new DistributionResponse(
				distribution.getIdDistribution(),
                distribution.getNumeroBordereau(),
                distribution.getDateDistribution(),
                distribution.getTypeDistribution(),
                distribution.getMotif(),
                distribution.getDestinataire(),
                distribution.getEtat(),
                distribution.getDateGenerationBordereau(),
                distribution.getDateSignature(),
                distribution.getSignePar(),
                distribution.getDemande().getIdDemande(),
                distribution.getDemande().getNumeroDemande(),
                distribution.getDemande().getObjet(),
                lignes
		);
	}
	
	private LigneDistributionResponse toLigneResponse(LigneDistribution ligne) {
        return new LigneDistributionResponse(
                ligne.getIdLigne(),
                ligne.getGadget().getIdGadget(),
                ligne.getGadget().getLibelle(),
                ligne.getQuantiteDistribuee()
        );
    }

}
