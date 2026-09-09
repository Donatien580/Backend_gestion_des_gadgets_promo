package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneDemandeRequest(
    @NotNull(message = "Le gadget est obligatoire.")
    Integer idGadget,

    @NotNull(message = "La quantité demandée est obligatoire.")
    @Min(value = 1, message = "La quantité demandée doit être supérieure à 0.")
    Integer quantiteDemandee
) {}
