package com.entreprise.gadgets.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApprovisionnementResponse(
    Integer idApprovisionnement,
    LocalDateTime dateReception,
    String fournisseur,
    String numeroPV,
    String observations,
    List<LigneApprovisionnementResponse> lignes,
    List<IncidentResponse> incidents
) {}
