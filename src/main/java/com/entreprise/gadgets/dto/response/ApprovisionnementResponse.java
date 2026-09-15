package com.entreprise.gadgets.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.entreprise.gadgets.model.enums.StatutApprovisionnement;

public record ApprovisionnementResponse(
		Integer idApprovisionnement,
	    LocalDateTime dateReception,
	    String fournisseur,
	    String adresseFournisseur,
	    String numeroPV,
	    String numeroMarche,
	    StatutApprovisionnement statut,
	    String observations,
	    List<LigneApprovisionnementResponse> lignes,
	    List<IncidentResponse> incidents
) {}
