package com.entreprise.gadgets.model.enums;

/**
 * Cycle de vie d'une demande : EN_ATTENTE -> VALIDEE_CHEF_DEPARTEMENT -> AFFECTEE -> TRAITEE
 * avec possibilité de REFUSEE ou ANNULEE à chaque étape de validation.
 */
public enum EtatDemande {
    EN_ATTENTE,
    VALIDEE_CHEF_DEPARTEMENT,
    AFFECTEE,
    REFUSEE,
    ANNULEE,
    TRAITEE
}
