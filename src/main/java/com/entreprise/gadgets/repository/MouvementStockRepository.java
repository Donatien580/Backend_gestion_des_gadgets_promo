package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.MouvementStock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {

    Page<MouvementStock> findByGadget_IdGadgetOrderByDateMouvementDesc(Integer idGadget, Pageable pageable);

    Page<MouvementStock> findByDateMouvementBetweenOrderByDateMouvementDesc(
        LocalDateTime debut, LocalDateTime fin, Pageable pageable);
    
    /**Version non paginée, pour les rapports**/
    //@Query("SELECT m FROM MouvementStock m WHERE m.dateMouvement BETWEEN :debut AND :fin ORDER BY m.dateMouvement DESC")
    @Query("SELECT m FROM MouvementStock m " +
    	       "JOIN FETCH m.gadget " +
    	       "LEFT JOIN FETCH m.utilisateur " +
    	       "WHERE m.dateMouvement BETWEEN :debut AND :fin " +
    	       "ORDER BY m.dateMouvement DESC")
    List<MouvementStock> listerPourRapport(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
