package com.entreprise.gadgets.dto.response;

import java.util.List;

public record TableauDeBordResponse(
    long totalGadgets,
    long totalCategories,
    long gadgetsEnAlerteCritique,
    long gadgetsEnAlerteAvertissement,
    long demandesEnAttente,
    long demandesAffectees,
    long approvisionnementsCeMois,
    long distributionsCeMois,
    List<GadgetAlerteResponse> gadgetsAlertes,
    List<NiveauStockGadgetResponse> niveauxStockGadgets,
    List<DemandeResponse> demandesRecentes
) {}