package com.entreprise.gadgets.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.entreprise.gadgets.model.enums.StatutApprovisionnement;

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
    
    @Column(name = "adresse_fournisseur", nullable = false, length = 255)
    private String adresseFournisseur;

    @Column(name = "numero_pv", length = 30)
    private String numeroPV;
    
    @Column(name= "numero_marche", length = 30)
    private String numeroMarche;
    
    @Enumerated(EnumType.STRING)
    @Column(name="statut", nullable=false, length=20)
    @Builder.Default
    private StatutApprovisionnement statut = StatutApprovisionnement.ENREGISTRE;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToMany(mappedBy = "approvisionnement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneApprovisionnement> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "approvisionnement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Incident> incidents = new ArrayList<>();
}
