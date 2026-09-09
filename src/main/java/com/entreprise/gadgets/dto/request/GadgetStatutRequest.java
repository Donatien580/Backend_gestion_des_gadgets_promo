package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotNull;

/** Corps de la requête PATCH .../statut pour activer/désactiver un gadget. */
public record GadgetStatutRequest(
    @NotNull(message = "Le statut actif est obligatoire.")
    Boolean actif
) {}
