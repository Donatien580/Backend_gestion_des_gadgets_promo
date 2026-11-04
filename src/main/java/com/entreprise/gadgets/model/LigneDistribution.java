package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ligne_distribution")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private Integer idLigne;

    @Column(name = "quantite_distribuee", nullable = false)
    private Integer quantiteDistribuee;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
    
    @Column(length = 500)
    private String motifCorrection;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ligne_remplacee")
    private LigneDistribution ligneRemplacee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gadget", nullable = false)
    private Gadget gadget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_distribution", nullable = false)
    private Distribution distribution;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie_beneficiaire", length = 30)
    private CategorieBeneficiaire categorieBeneficiaire;   // null = distribution à une seule personne

    @Column(name = "nombre_beneficiaires", nullable = false)
    @Builder.Default
    private Integer nombreBeneficiaires = 1;

    @Column(name = "quantite_par_personne", nullable = false)
    private Integer quantiteParPersonne;
}