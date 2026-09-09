package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Approvisionnement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApprovisionnementRepository extends JpaRepository<Approvisionnement, Integer> {

    Page<Approvisionnement> findAllByOrderByDateReceptionDesc(Pageable pageable);

    @Query("SELECT a FROM Approvisionnement a WHERE :recherche IS NULL OR LOWER(a.fournisseur) LIKE LOWER(CONCAT('%', CAST(:recherche AS string), '%')) ORDER BY a.dateReception DESC")
    Page<Approvisionnement> rechercherParFournisseur(String recherche, Pageable pageable);
}