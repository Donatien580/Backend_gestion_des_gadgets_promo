package com.entreprise.gadgets.dto.response;

public record GadgetAlerteResponse(
    Integer idGadget,
    String libelle,
    String categorie,
    Integer quantiteDisponible,
    Integer seuilAlerte,
    String niveau // "CRITIQUE" | "AVERTISSEMENT"
) {}