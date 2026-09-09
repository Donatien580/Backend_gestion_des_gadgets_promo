package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    private Integer idCategorie;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
