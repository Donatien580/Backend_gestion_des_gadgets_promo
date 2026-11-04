package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.LigneInventaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LigneInventaireRepository extends JpaRepository<LigneInventaire, Integer> {
    List<LigneInventaire> findByInventaire_IdInventaire(Integer idInventaire);
    
    Optional<LigneInventaire> findByInventaire_IdInventaireAndGadget_IdGadget(
            Integer idInventaire, Integer idGadget
        );
}
