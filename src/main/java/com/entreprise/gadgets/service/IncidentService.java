package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.response.IncidentResponse;
import com.entreprise.gadgets.model.enums.StatutIncident;

import java.util.List;

public interface IncidentService {

    List<IncidentResponse> lister(StatutIncident statut);

    IncidentResponse marquerResolu(Integer id);
}
