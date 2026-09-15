package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotNull;

public record AffectationDemandeRequest(
	@NotNull(message = "L'agent à affecter est obligatoire.")
	Integer idAgentAffecte
) {}
