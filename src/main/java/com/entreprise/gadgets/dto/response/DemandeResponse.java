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
    EtatDemande etat,
    String motifRefus,
    String observations,
    
    // Interne
    Integer idService,
    String libelleService,
    String matriculeResponsable,
    String nomResponsable,
    Integer nombrePersonnelsImpactes,

    // Externe
    String structure,
    String representant,
    String telephone,

    // Agent affecté
    Integer idAgentAffecte,
    String nomAgentAffecte,
    
    PieceJustificativeResponse pieceJustificative,

    List<LigneDemandeResponse> lignes
) {}