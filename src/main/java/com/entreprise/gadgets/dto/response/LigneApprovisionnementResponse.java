package com.entreprise.gadgets.dto.response;

public record LigneApprovisionnementResponse(
	Integer idLigne,
	GadgetResume gadget,
	Integer quantiteCommandee,
	Integer quantiteRecue,
	Integer quantiteDefectueuse,
	String observationQualite,
	Boolean actif,
	Integer idLigneRemplacee,
	String motifCorrection
) {}
