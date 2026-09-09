package com.entreprise.gadgets.dto.response;

public record LigneInventaireResponse(
		Integer idLigne,
		Integer idGadget,
		String libelleGadget,
		Integer stockTheorique,
		Integer stockReel,
		Integer ecart,
		String justification,
		Boolean validationJustif		
) {}
