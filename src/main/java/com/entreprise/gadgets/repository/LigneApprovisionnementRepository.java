package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.LigneApprovisionnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneApprovisionnementRepository extends JpaRepository<LigneApprovisionnement, Integer> {
    List<LigneApprovisionnement> findByApprovisionnement_IdApprovisionnement(Integer idApprovisionnement);
    List<LigneApprovisionnement> findByApprovisionnement_IdApprovisionnementAndActifTrue(Integer idApprovisionnnement);
}
