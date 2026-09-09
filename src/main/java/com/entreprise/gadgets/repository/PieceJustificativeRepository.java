package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Integer> {
    Optional<PieceJustificative> findByDemande_IdDemande(Integer idDemande);
}
