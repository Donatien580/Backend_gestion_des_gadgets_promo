package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ServiceDemandeurRepository extends JpaRepository<Services, Integer> {

    boolean existsByCodeService(String codeService);

    @Query("SELECT s FROM Services s LEFT JOIN FETCH s.personnels WHERE s.idService = :id")
    Optional<Services> findByIdWithPersonnels(@Param("id") Integer id);
}