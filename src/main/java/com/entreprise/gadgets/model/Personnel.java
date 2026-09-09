package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personnel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personnel")
    private Integer idPersonnel;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "prenom", length = 50)
    private String prenom;

    @Column(name = "matricule", length = 20, unique = true)
    private String matricule;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "fonction", length = 100)
    private String fonction;

    @Column(name = "actif")
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service", nullable = false)
    private Services service;
}