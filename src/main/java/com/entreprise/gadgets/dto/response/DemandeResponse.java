package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.TypeDemande;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DemandeResponse(
		Integer idDemande,
	    String numeroDemande,
	    String objet,
	    TypeDemande typeDemande,
	    LocalDateTime dateDemande,
	    LocalDate dateSouhaitee,
	    LocalDateTime dateValidation,
	    LocalDateTime dateTraitement,
	    EtatDemande etat,
	    String motifRefus,
	    String observations,

	    String nomDemandeur,
	    String prenomDemandeur,
	    String telephoneDemandeur,

	    // Interne
	    String matriculeDemandeur,
	    String serviceDemandeur,

	    // Externe
	    String structureDemandeur,

	    AgentResume agentSaisie,
	    AgentResume agentAffecte,

	    PieceJustificativeResponse pieceJustificative
) {}