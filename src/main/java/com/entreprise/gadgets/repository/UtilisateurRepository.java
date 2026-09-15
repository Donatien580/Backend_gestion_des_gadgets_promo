package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByRole_NomInAndActifTrueOrderByNomAsc(List<RoleType> roles);
}
