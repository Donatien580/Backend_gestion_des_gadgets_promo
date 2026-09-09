package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.EtatInventaire;

import com.entreprise.gadgets.model.enums.TypeInventaire;

import java.time.LocalDate;
import java.util.List;

public record InventaireResponse(
    Integer idInventaire,
    LocalDate dateInventaire,
    TypeInventaire typeInventaire,
    EtatInventaire etat,
    String observations,
    String realisateur,
    List<LigneInventaireResponse> lignes,
    LocalDate dateCreation
) {}