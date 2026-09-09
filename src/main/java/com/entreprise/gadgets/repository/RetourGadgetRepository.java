package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.RetourGadget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetourGadgetRepository extends JpaRepository<RetourGadget, Integer> {
    Page<RetourGadget> findByGadget_IdGadget(Integer idGadget, Pageable pageable);
}
