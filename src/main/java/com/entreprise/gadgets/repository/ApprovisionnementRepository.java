package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Approvisionnement;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApprovisionnementRepository extends JpaRepository<Approvisionnement, Integer> {

	long countByDateReceptionAfter(LocalDateTime date);
	
	Page<Approvisionnement> findAllByOrderByDateReceptionDesc(Pageable pageable);

    @Query("SELECT a FROM Approvisionnement a WHERE :recherche IS NULL OR LOWER(a.fournisseur) LIKE LOWER(CONCAT('%', CAST(:recherche AS string), '%')) ORDER BY a.dateReception DESC")
    Page<Approvisionnement> rechercherParFournisseur(String recherche, Pageable pageable);
    
    @Query("SELECT DISTINCT a.fournisseur FROM Approvisionnement a WHERE LOWER(a.fournisseur) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY a.fournisseur")
    List<String> suggererFournisseurs(@Param("prefixe") String prefixe, Pageable pageable);
}