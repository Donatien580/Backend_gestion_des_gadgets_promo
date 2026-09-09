package com.entreprise.gadgets.model.enums;

/**
 * Nature d'un mouvement de stock.
 * ENTREE : approvisionnement | SORTIE : distribution
 * RETOUR : réintégration de gadgets | INVENTAIRE : régularisation suite à écart
 */
public enum TypeMouvement {
    ENTREE,
    SORTIE,
    RETOUR,
    INVENTAIRE
}
