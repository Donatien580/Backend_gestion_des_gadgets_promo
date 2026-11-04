package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneGroupeRequest(
		@NotNull Integer idGadget,
	    @NotNull @Min(1) @Max(10000) Integer quantiteParPersonne) {

}
