package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.EtatDistribution;
import com.entreprise.gadgets.model.enums.TypeDistribution;
import java.time.LocalDateTime;
import java.util.List;

public record DistributionResponse(
    Integer idDistribution,
    String numeroBordereau,
    LocalDateTime dateDistribution,
    TypeDistribution typeDistribution,
    String motif,
    String destinataire,
    EtatDistribution etat,
    LocalDateTime dateGenerationBordereau,
    LocalDateTime dateSignature,
    String signePar,
    Integer idDemande,
    String numeroDemande,
    String objetDemande,
    List<LigneDistributionResponse> lignes
) {}