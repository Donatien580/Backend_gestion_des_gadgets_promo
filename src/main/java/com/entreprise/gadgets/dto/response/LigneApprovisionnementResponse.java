package com.entreprise.gadgets.dto.response;

public record LigneApprovisionnementResponse(
    Integer idLigne,
    GadgetResume gadget,
    Integer quantiteCommandee,
    Integer quantiteRecue,
    Integer quantiteConforme,
    Integer quantiteDefectueuse,
    String observationQualite
) {}
