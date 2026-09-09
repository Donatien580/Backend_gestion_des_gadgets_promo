package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Rôle applicatif. Table synchronisée avec les rôles définis dans Keycloak
 * (cf. section 2.1 de la documentation technique).
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Integer idRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "nom", nullable = false, unique = true, length = 50)
    private RoleType nom;

    @Column(name = "description", length = 255)
    private String description;
}
