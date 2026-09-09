package com.entreprise.gadgets.dto.response;

public record LigneDistributionResponse(
    Integer idLigne,
    Integer idGadget,
    String libelleGadget,
    Integer quantiteDistribuee
) {}