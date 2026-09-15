package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Distribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DistributionRepository extends JpaRepository<Distribution, Integer> {
    
	long countByDateDistributionAfter(LocalDateTime date);
	
	boolean existsByNumeroBordereau(String numero);
    Optional<Distribution> findByDemande_IdDemande(Integer idDemande);
    //Page<Distribution> findAll(Pageable pageable);
    
    @Query("SELECT DISTINCT d.nomReceptionnaire FROM Distribution d WHERE d.nomReceptionnaire IS NOT NULL AND LOWER(d.nomReceptionnaire) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.nomReceptionnaire")
    List<String> suggererNomsReceptionnaire(@Param("prefixe") String prefixe, Pageable pageable);

    @Query("SELECT DISTINCT d.prenomReceptionnaire FROM Distribution d WHERE d.prenomReceptionnaire IS NOT NULL AND LOWER(d.prenomReceptionnaire) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.prenomReceptionnaire")
    List<String> suggererPrenomsReceptionnaire(@Param("prefixe") String prefixe, Pageable pageable);

    @Query("SELECT DISTINCT d.serviceReceptionnaire FROM Distribution d WHERE d.serviceReceptionnaire IS NOT NULL AND LOWER(d.serviceReceptionnaire) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.serviceReceptionnaire")
    List<String> suggererServicesReceptionnaire(@Param("prefixe") String prefixe, Pageable pageable);

    @Query("SELECT DISTINCT d.destinataire FROM Distribution d WHERE d.destinataire IS NOT NULL AND LOWER(d.destinataire) LIKE LOWER(CONCAT(:prefixe, '%')) ORDER BY d.destinataire")
    List<String> suggererDestinataires(@Param("prefixe") String prefixe, Pageable pageable);
}
