package com.entreprise.gadgets.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approvisionnement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Approvisionnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_approvisionnement")
    private Integer idApprovisionnement;

    @Column(name = "date_reception", nullable = false)
    private LocalDateTime dateReception;

    @Column(name = "fournisseur", nullable = false, length = 100)
    private String fournisseur;

    @Column(name = "numero_pv", length = 30)
    private String numeroPV;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToMany(mappedBy = "approvisionnement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneApprovisionnement> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "approvisionnement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Incident> incidents = new ArrayList<>();
}
