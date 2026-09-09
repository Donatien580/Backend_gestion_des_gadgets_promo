package com.entreprise.gadgets.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TraitementDemandeRequest(
    @NotEmpty(message = "Au moins une ligne à traiter est requise.")
    @Valid
    List<TraitementLigneRequest> lignes
) {}
