package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.EtatDistribution;
import com.entreprise.gadgets.model.enums.TypeDistribution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "distribution")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Distribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_distribution")
    private Integer idDistribution;

    @Column(name = "numero_bordereau", unique = true, nullable = false, length = 20)
    private String numeroBordereau;

    @Column(name = "date_distribution")
    private LocalDateTime dateDistribution;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_distribution", nullable = false, length = 10)
    private TypeDistribution typeDistribution;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    /** Libellé libre du destinataire pour une distribution EXTERNE. */
    @Column(name = "destinataire", length = 255)
    private String destinataire;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    private EtatDistribution etat;

    @Column(name = "date_generation_bordereau")
    private LocalDateTime dateGenerationBordereau;

    /** true = dotation (aucune demande associée). */
    @Column(name = "est_dotation", nullable = false)
    @Builder.Default
    private Boolean estDotation = false;

    // Réceptionnaire (distribution INTERNE, liée à une demande ou en dotation)
    @Column(name = "matricule_receptionnaire", length = 20)
    private String matriculeReceptionnaire;

    @Column(name = "nom_receptionnaire", length = 100)
    private String nomReceptionnaire;

    @Column(name = "prenom_receptionnaire", length = 100)
    private String prenomReceptionnaire;

    @Column(name = "service_receptionnaire", length = 100)
    private String serviceReceptionnaire;

    @Column(name = "nombre_personnes")
    private Integer nombrePersonnes;

    /** Nullable : absente pour une dotation. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", unique = true)
    private Demande demande;

    @OneToMany(mappedBy = "distribution", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneDistribution> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dateDistribution == null) dateDistribution = LocalDateTime.now();
        if (etat == null) etat = EtatDistribution.EN_ATTENTE;
    }

    public void ajouterLigne(LigneDistribution ligne) {
        ligne.setDistribution(this);
        this.lignes.add(ligne);
    }
}