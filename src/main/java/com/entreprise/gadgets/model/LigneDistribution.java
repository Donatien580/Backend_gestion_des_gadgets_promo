package com.entreprise.gadgets.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gadget", nullable = false)
    private Gadget gadget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_distribution", nullable = false)
    private Distribution distribution;
}