package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Incident;
import com.entreprise.gadgets.model.enums.StatutIncident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    List<Incident> findByStatut(StatutIncident statut);
}
