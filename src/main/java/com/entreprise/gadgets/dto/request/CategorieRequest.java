package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Données saisies pour créer ou modifier une catégorie.
 * Un DTO dédié (plutôt que d'exposer l'entité directement) protège l'API des
 * changements internes du modèle de données et permet des règles de
 * validation propres à la saisie utilisateur.
 */
public record CategorieRequest(

    @NotBlank(message = "Le libellé est obligatoire.")
    @Size(max = 50, message = "Le libellé ne doit pas dépasser 50 caractères.")
    String libelle,

    String description
) {}
