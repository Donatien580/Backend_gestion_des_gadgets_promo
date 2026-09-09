package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.MouvementStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {

    Page<MouvementStock> findByGadget_IdGadgetOrderByDateMouvementDesc(Integer idGadget, Pageable pageable);

    Page<MouvementStock> findByDateMouvementBetweenOrderByDateMouvementDesc(
        LocalDateTime debut, LocalDateTime fin, Pageable pageable);
}
