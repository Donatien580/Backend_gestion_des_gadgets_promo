package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneInventaireRequest(
    @NotNull(message = "Le gadget est obligatoire")
    Integer idGadget,

    @NotNull(message = "Le stock réel compté est obligatoire")
    @Min(value = 0, message = "Le stock réel ne peut pas être négatif")
    Integer stockReel
) {}