package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 50)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mot_de_passe_hash", length = 255)
    private String motDePasseHash;

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;
}
