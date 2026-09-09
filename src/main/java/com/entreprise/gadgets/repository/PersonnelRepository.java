package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {

    boolean existsByMatricule(String matricule);

    List<Personnel> findByService_IdServiceAndActifTrue(Integer idService);

    long countByService_IdServiceAndActifTrue(Integer idService);

    @Query("SELECT p FROM Personnel p JOIN FETCH p.service WHERE p.idPersonnel = :id")
    Optional<Personnel> findByIdWithService(@Param("id") Integer id);
}