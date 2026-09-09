package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.response.IncidentResponse;
import com.entreprise.gadgets.model.enums.StatutIncident;
import com.entreprise.gadgets.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public List<IncidentResponse> lister(@RequestParam(required = false) StatutIncident statut) {
        return incidentService.lister(statut);
    }

    @PatchMapping("/{id}/resoudre")
    public IncidentResponse marquerResolu(@PathVariable Integer id) {
        return incidentService.marquerResolu(id);
    }
}
