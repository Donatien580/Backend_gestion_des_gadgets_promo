package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JustificationRequest(
    @NotBlank(message = "La justification est obligatoire")
    String justification
) {}