package com.entreprise.gadgets.dto.response;

import java.util.List;

import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;

public record GroupeBeneficiaireResponse(
   CategorieBeneficiaire categorie, 
   String libelleCategorie,
   Integer nombre, 
   List<String> noms,
   List<LigneGroupeResponse> ligne
) {}
