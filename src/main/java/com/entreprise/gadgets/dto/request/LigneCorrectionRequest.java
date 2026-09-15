package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LigneCorrectionRequest(	
		@NotNull(message = "La ligne à corriger est obligatoire.")
	    Integer idLigne,

	    Integer quantiteCommandee,

	    @NotNull(message = "La quantité reçue corrigée est obligatoire.")
	    @Min(value = 1, message = "La quantité reçue doit être supérieure à 0.")
	    Integer quantiteRecue,

	    @Min(value = 0, message = "La quantité défectueuse ne peut pas être négative.")
	    Integer quantiteDefectueuse,

	    String observationQualite,

	    @NotBlank(message = "Le motif de la correction est obligatoire.")
	    String motifCorrection
) {}
