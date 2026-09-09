package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.entreprise.gadgets.model.enums.EtatDistribution;
import com.entreprise.gadgets.model.enums.TypeDistribution;

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

    @Column(name = "destinataire", length = 255)
    private String destinataire;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    private EtatDistribution etat;

    @Column(name = "date_generation_bordereau")
    private LocalDateTime dateGenerationBordereau;

    @Column(name = "date_signature")
    private LocalDateTime dateSignature;

    @Column(name = "signe_par", length = 100)
    private String signePar;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, unique = true)
    private Demande demande;

    @OneToMany(mappedBy = "distribution", cascade = CascadeType.ALL, orphanRemoval = true)
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