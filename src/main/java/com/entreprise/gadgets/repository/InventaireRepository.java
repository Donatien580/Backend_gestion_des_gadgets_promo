package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Inventaire;
import com.entreprise.gadgets.model.enums.EtatInventaire;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventaireRepository extends JpaRepository<Inventaire, Integer> {
    Page<Inventaire> findByEtat(EtatInventaire etat, Pageable pageable);
    @EntityGraph(attributePaths = {"lignes", "lignes.gadget", "realisateur"})
    Optional<Inventaire> findWithLignesByIdInventaire(Integer idInventaire);
}
