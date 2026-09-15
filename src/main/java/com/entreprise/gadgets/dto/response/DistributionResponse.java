package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.EtatDistribution;
import com.entreprise.gadgets.model.enums.TypeDistribution;
import java.time.LocalDateTime;
import java.util.List;

public record DistributionResponse(
		Integer idDistribution,
	    String numeroBordereau,
	    LocalDateTime dateDistribution,
	    TypeDistribution typeDistribution,
	    Boolean estDotation,
	    String motif,
	    String destinataire,
	    String matriculeReceptionnaire,
	    String nomReceptionnaire,
	    String prenomReceptionnaire,
	    String serviceReceptionnaire,
	    Integer nombrePersonnes,
	    EtatDistribution etat,
	    LocalDateTime dateGenerationBordereau,
	    Integer idDemande,
	    String numeroDemande,
	    String objetDemande,
	    List<LigneDistributionResponse> lignes
) {}