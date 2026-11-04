package com.entreprise.gadgets.dto.request;

import java.util.List;

import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record GroupeBeneficiaireRequest(
	CategorieBeneficiaire categorie,
	@Min(1) @Max(10000) Integer nombre,
	@NotEmpty @Valid List<LigneGroupeRequest> lignes,
	List<@Size(max = 150) String> noms) {

}
