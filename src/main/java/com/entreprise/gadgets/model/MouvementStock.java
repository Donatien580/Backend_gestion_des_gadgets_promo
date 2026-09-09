package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Journal d'audit immuable de tout mouvement de stock. Chaque ligne est créée
 * exclusivement par StockService, jamais modifiée ni supprimée par la suite.
 */
@Entity
@Table(name = "mouvement_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement")
    private Integer idMouvement;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false, length = 10)
    private TypeMouvement typeMouvement;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "stock_avant", nullable = false)
    private Integer stockAvant;

    @Column(name = "stock_apres", nullable = false)
    private Integer stockApres;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gadget", nullable = false)
    private Gadget gadget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    /** Id de l'entité source (Approvisionnement, Distribution, Retour, Inventaire). */
    @Column(name = "id_reference")
    private Integer idReference;

    /** Nom de l'entité source, ex: "APPROVISIONNEMENT", "DISTRIBUTION". */
    @Column(name = "type_reference", length = 30)
    private String typeReference;
}
