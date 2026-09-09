package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefusDemandeRequest(
    @NotBlank(message = "Le motif de refus est obligatoire.")
    String motifRefus
) {}
