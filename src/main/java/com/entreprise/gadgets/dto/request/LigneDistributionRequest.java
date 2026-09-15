package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneDistributionRequest(
	@NotNull(message = "Le gadget est obligatoire.")
    Integer idGadget,

	@NotNull(message = "La quantité à distribuer est obligatoire.")
	@Min(value = 1, message = "La quantité doit être supérieure à 0.")
	Integer quantiteDistribuee) {}
