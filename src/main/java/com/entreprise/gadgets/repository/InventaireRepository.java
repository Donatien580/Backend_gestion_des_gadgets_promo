package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Inventaire;
import com.entreprise.gadgets.model.enums.EtatInventaire;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventaireRepository extends JpaRepository<Inventaire, Integer> {
    Page<Inventaire> findByEtat(EtatInventaire etat, Pageable pageable);
    @EntityGraph(attributePaths = {"lignes", "lignes.gadget", "realisateur"})
    Optional<Inventaire> findWithLignesByIdInventaire(Integer idInventaire);
    
    @Query("""
    	    SELECT i FROM Inventaire i
    	    LEFT JOIN i.realisateur r
    	    WHERE LOWER(i.observations) LIKE LOWER(CONCAT('%', :recherche, '%'))
    	       OR LOWER(r.nom) LIKE LOWER(CONCAT('%', :recherche, '%'))
    	       OR LOWER(r.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))
    	    """)
    	Page<Inventaire> rechercher(@Param("recherche") String recherche, Pageable pageable);
}
