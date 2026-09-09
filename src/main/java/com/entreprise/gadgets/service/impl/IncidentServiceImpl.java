package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.response.IncidentResponse;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.IncidentMapper;
import com.entreprise.gadgets.model.Incident;
import com.entreprise.gadgets.model.enums.StatutIncident;
import com.entreprise.gadgets.repository.IncidentRepository;
import com.entreprise.gadgets.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<IncidentResponse> lister(StatutIncident statut) {
        List<Incident> incidents = statut != null
            ? incidentRepository.findByStatut(statut)
            : incidentRepository.findAll();
        return incidents.stream().map(incidentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public IncidentResponse marquerResolu(Integer id) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Incident", id));
        incident.setStatut(StatutIncident.RESOLU);
        return incidentMapper.toResponse(incident);
    }
}
