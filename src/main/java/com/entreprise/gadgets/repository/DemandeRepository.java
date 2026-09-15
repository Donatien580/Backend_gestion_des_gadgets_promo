package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Demande;
import com.entreprise.gadgets.model.enums.EtatDemande;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {

    boolean existsByNumeroDemande(String numeroDemande);

    long countByEtat(EtatDemande etat);
    List<Demande> findTop5ByOrderByDateDemandeDesc();
    
    Page<Demande> findAll(Pageable pageable);

    Page<Demande> findByEtat(EtatDemande etat, Pageable pageable);

    // Recherche uniquement (numéro ou objet)
    @Query("SELECT d FROM Demande d WHERE LOWER(d.numeroDemande) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(d.objet) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    Page<Demande> rechercher(@Param("recherche") String recherche, Pageable pageable);

    // Filtre + recherche
    @Query("SELECT d FROM Demande d WHERE d.etat = :etat AND " +
           "(LOWER(d.numeroDemande) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(d.objet) LIKE LOWER(CONCAT('%', :recherche, '%')))")
    Page<Demande> findByEtatAndRecherche(@Param("etat") EtatDemande etat,
                                         @Param("recherche") String recherche,
                                         Pageable pageable);
    
    @Query("SELECT DISTINCT d.nomDemandeur FROM Demande d WHERE LOWER(d.nomDemandeur) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.nomDemandeur")
    List<String> suggererNoms(@Param("prefixe") String prefixe, Pageable pageable);

    @Query("SELECT DISTINCT d.prenomDemandeur FROM Demande d WHERE d.prenomDemandeur IS NOT NULL AND LOWER(d.prenomDemandeur) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.prenomDemandeur")
    List<String> suggererPrenoms(@Param("prefixe") String prefixe, Pageable pageable);

    @Query("SELECT DISTINCT d.serviceDemandeur FROM Demande d WHERE d.serviceDemandeur IS NOT NULL AND LOWER(d.serviceDemandeur) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.serviceDemandeur")
    List<String> suggererServices(@Param("prefixe") String prefixe, Pageable pageable);

    @Query("SELECT DISTINCT d.structureDemandeur FROM Demande d WHERE d.structureDemandeur IS NOT NULL AND LOWER(d.structureDemandeur) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.structureDemandeur")
    List<String> suggererStructures(@Param("prefixe") String prefixe, Pageable pageable);
}