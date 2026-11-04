package com.entreprise.gadgets.dto.response;

public record LigneGroupeResponse(
	Integer idGadget, 
	String libelleGadget,
	Integer quantiteParPersonne, 
	Integer quantiteTotale
) {}
