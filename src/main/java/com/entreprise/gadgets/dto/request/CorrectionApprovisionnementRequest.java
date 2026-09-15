package com.entreprise.gadgets.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CorrectionApprovisionnementRequest(
	@NotEmpty(message = "Au moins une ligne à corriger est requise.")
	@Valid
	List<LigneCorrectionRequest> lignes
) {}
