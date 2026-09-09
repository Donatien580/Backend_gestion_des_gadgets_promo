package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Role;
import com.entreprise.gadgets.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNom(RoleType nom);
}
