package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TraitementLigneRequest(
    @NotNull Integer idLigne,

    @NotNull(message = "La quantité accordée est obligatoire.")
    @Min(value = 0, message = "La quantité accordée ne peut pas être négative.")
    Integer quantiteAccordee
) {}
