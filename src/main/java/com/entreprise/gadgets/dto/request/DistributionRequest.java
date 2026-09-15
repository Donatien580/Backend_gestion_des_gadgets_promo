package com.entreprise.gadgets.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import com.entreprise.gadgets.model.enums.TypeDistribution;

public record DistributionRequest(
		 /** Null pour une dotation (aucune demande associée). */
	    Integer idDemande,

	    /** Obligatoire uniquement si idDemande est null (dotation) — sinon déduit de la demande. */
	    TypeDistribution typeDistribution,

	    LocalDateTime dateDistribution,
	    String motif,

	    /* EXTERNE : libellé libre du destinataire */
	    String destinataire,

	    /* INTERNE (avec ou sans demande) : informations du réceptionnaire */
	    String matriculeReceptionnaire,
	    String nomReceptionnaire,
	    String prenomReceptionnaire,
	    String serviceReceptionnaire,
	    Integer nombrePersonnes,

	    @NotEmpty(message = "Au moins un gadget doit être sélectionné.")
	    @Valid
	    List<LigneDistributionRequest> lignes
) {}
