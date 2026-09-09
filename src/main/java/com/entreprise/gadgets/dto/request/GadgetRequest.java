package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Données saisies pour créer ou modifier un gadget du catalogue.
 * Ne contient volontairement pas quantiteDisponible : le stock n'est jamais
 * modifié via ce DTO, uniquement par StockService (approvisionnements,
 * distributions, retours, inventaires).
 */
public record GadgetRequest(

    @NotBlank(message = "Le libellé est obligatoire.")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères.")
    String libelle,

    @Size(max = 30, message = "La désignation ne doit pas dépasser 30 caractères.")
    String designation,

    String description,

    @NotNull(message = "Le seuil d'alerte est obligatoire.")
    @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif.")
    Integer seuilAlerte,

    @NotNull(message = "La catégorie est obligatoire.")
    Integer idCategorie
) {}
