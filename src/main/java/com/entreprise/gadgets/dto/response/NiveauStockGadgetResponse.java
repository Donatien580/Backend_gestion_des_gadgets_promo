package com.entreprise.gadgets.dto.response;

public record NiveauStockGadgetResponse(
    Integer idGadget,
    String libelle,
    String categorie,
    Integer quantiteDisponible
) {}