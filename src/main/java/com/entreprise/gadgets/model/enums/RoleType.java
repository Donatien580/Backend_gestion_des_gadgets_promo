package com.entreprise.gadgets.model.enums;

/**
 * Rôles applicatifs. Synchronisés avec les rôles Keycloak (ex: ROLE_ADMIN)
 * lors de l'intégration de l'authentification (cf. consigne 1 - à venir).
 */
public enum RoleType {
    ADMIN,
    AGENT_SAISIE,
    CHEF_DEPARTEMENT,
    CHEF_SERVICE,
    GESTIONNAIRE_STOCK
}
