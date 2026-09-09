package com.entreprise.gadgets.dto.request;

import java.time.LocalDate;


import com.entreprise.gadgets.model.enums.TypeInventaire;

import jakarta.validation.constraints.NotNull;

public record InventaireRequest(
		@NotNull(message="La date de l'inventaire est obligatoire")
		LocalDate dateInventaire,
		
		@NotNull(message="Le type d'inventaire est obligatoire")
		TypeInventaire typeInventaire,
		
		String observations
) {}
