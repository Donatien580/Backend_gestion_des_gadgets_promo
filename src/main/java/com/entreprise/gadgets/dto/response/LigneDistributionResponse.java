package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;

public record LigneDistributionResponse(
    Integer idLigne,
    Integer idGadget,
    String libelleGadget,
    CategorieBeneficiaire categorie, 
    Integer nombreBeneficiaires, 
    Integer quantiteParPersonne,
    Integer quantiteDistribuee,
    Boolean actif,
    String motifCorrection
) {}