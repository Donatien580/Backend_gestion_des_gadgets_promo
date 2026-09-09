package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.StatutIncident;
import com.entreprise.gadgets.model.enums.TypeIncident;

import java.time.LocalDateTime;

public record IncidentResponse(
    Integer idIncident,
    TypeIncident typeIncident,
    String description,
    LocalDateTime dateSignalement,
    StatutIncident statut
) {}
