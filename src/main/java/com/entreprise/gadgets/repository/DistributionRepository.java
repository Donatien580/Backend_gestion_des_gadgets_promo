package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Distribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistributionRepository extends JpaRepository<Distribution, Integer> {
    /*Optional<Distribution> findByNumeroBordereau(String numeroBordereau);
    Optional<Distribution> findByDemande_IdDemande(Integer idDemande);
    Page<Distribution> findAllByOrderByDateDistributionDesc(Pageable pageable);*/
	
    boolean existsByNumeroBordereau(String numero);
    Optional<Distribution> findByDemande_IdDemande(Integer idDemande);
    Page<Distribution> findAll(Pageable pageable);
}
