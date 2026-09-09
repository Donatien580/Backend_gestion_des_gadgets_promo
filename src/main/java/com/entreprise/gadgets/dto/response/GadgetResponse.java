package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.EtatGadget;

public record GadgetResponse(
    Integer idGadget,
    String libelle,
    String designation,
    String description,
    Integer seuilAlerte,
    String photoGadget,
    Integer quantiteDisponible,
    EtatGadget etat,
    Boolean actif,
    CategorieResponse categorie,

    /** Calculé à partir de quantiteDisponible et seuilAlerte, jamais stocké. */
    boolean sousSeuilAlerte
) {}
