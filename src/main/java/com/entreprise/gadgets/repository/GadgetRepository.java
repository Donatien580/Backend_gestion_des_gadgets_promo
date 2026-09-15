package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Gadget;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface GadgetRepository extends JpaRepository<Gadget, Integer> {

    @Query("""
        SELECT g FROM Gadget g
        WHERE (:idCategorie IS NULL OR g.categorie.idCategorie = :idCategorie)
        AND (:inclureInactifs = true OR g.actif = true)
        AND (:recherche IS NULL OR LOWER(g.libelle) LIKE LOWER(CONCAT('%', CAST(:recherche AS string), '%')))
        """)
    Page<Gadget> rechercher(Integer idCategorie, boolean inclureInactifs, String recherche, Pageable pageable);

    boolean existsByCategorie_IdCategorie(Integer idCategorie);

    @Query("SELECT g FROM Gadget g WHERE g.actif = true AND g.quantiteDisponible <= g.seuilAlerte")
    List<Gadget> findGadgetsSousSeuilAlerte();
    
    List<Gadget> findByActifTrue();
    
    List<Gadget> findTop10ByActifTrueOrderByQuantiteDisponibleDesc();
    
}
