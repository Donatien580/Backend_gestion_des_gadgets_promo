package com.entreprise.gadgets.dto.response;

public record LigneDemandeResponse(
    Integer idLigne,
    Integer idGadget,
    String libelleGadget,
    Integer quantiteDemandee,
    Integer quantiteAccordee
    //GadgetResume gadget,
) {}
