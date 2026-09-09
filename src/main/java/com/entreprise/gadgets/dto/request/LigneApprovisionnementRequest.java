package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneApprovisionnementRequest(

    @NotNull(message = "Le gadget est obligatoire.")
    Integer idGadget,

    @Min(value = 0, message = "La quantité commandée ne peut pas être négative.")
    Integer quantiteCommandee,

    @NotNull(message = "La quantité reçue est obligatoire.")
    @Min(value = 1, message = "La quantité reçue doit être supérieure à 0.")
    Integer quantiteRecue,

    @Min(value = 0, message = "La quantité défectueuse ne peut pas être négative.")
    Integer quantiteDefectueuse,

    String observationQualite
) {}
