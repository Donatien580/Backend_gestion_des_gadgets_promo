package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.LigneDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneDemandeRepository extends JpaRepository<LigneDemande, Integer> {
    List<LigneDemande> findByDemande_IdDemande(Integer idDemande);
}
