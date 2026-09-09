package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ligne_approvisionnement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneApprovisionnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private Integer idLigne;

    @Column(name = "quantite_commandee")
    private Integer quantiteCommandee;

    @Column(name = "quantite_recue", nullable = false)
    private Integer quantiteRecue;

    @Column(name = "quantite_conforme")
    private Integer quantiteConforme;

    @Column(name = "quantite_defectueuse")
    private Integer quantiteDefectueuse;

    @Column(name = "observation_qualite", length = 255)
    private String observationQualite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gadget", nullable = false)
    private Gadget gadget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_approvisionnement", nullable = false)
    private Approvisionnement approvisionnement;
}
