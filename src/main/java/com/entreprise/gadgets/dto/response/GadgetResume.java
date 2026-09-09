package com.entreprise.gadgets.dto.response;

/**
 * Résumé minimal d'un gadget (id + libellé), utilisé partout où une ligne
 * fait référence à un gadget sans avoir besoin de tous ses détails
 * (catégorie, stock, etc.) : LigneApprovisionnementResponse,
 * LigneInventaireResponse, MouvementStockResponse...
 */
public record GadgetResume(
    Integer idGadget,
    String libelle
) {}
