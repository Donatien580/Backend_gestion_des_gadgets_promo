package com.entreprise.gadgets.dto.response;

import java.util.List;

public record ServiceResponse(
		Integer idService,
		String libelleService,
		String codeService,
		String matriculeResponsable,
		String nomResponsable,
	    String telephoneResponsable,
	    List<PersonnelResponse> personnels
) {}
