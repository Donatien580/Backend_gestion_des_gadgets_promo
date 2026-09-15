package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.TypeDemande;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "demande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Integer idDemande;

    @Column(name = "numero_demande", unique = true, nullable = false, length = 20)
    private String numeroDemande;

    @Column(name = "objet", nullable = false, length = 200)
    private String objet;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_demande", nullable = false, length = 20)
    private TypeDemande typeDemande;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_souhaitee")
    private LocalDate dateSouhaitee;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    /** Renseignée plus tard par le module Distribution, quand la demande est honorée. */
    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 30)
    private EtatDemande etat;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    // Informations du demandeur saisies directement (plus de Personnel/Service)
    @Column(name = "nom_demandeur", nullable = false, length = 100)
    private String nomDemandeur;

    @Column(name = "prenom_demandeur", length = 100)
    private String prenomDemandeur;

    @Column(name = "telephone_demandeur", length = 20)
    private String telephoneDemandeur;

    /** Renseigné uniquement si typeDemande = INTERNE */
    @Column(name = "matricule_demandeur", length = 20)
    private String matriculeDemandeur;

    @Column(name = "service_demandeur", length = 100)
    private String serviceDemandeur;

    /** Renseigné uniquement si typeDemande = EXTERNE */
    @Column(name = "structure_demandeur", length = 150)
    private String structureDemandeur;

    /** Utilisateur qui a saisi la demande. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur agentSaisie;

    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private PieceJustificative pieceJustificative;

    /** Agent (Chef Service / Gestionnaire de stock) à qui le Chef Département confie la demande. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent_affecte")
    private Utilisateur agentAffecte;

    @PrePersist
    protected void onCreate() {
        if (dateDemande == null) dateDemande = LocalDateTime.now();
        if (etat == null) etat = EtatDemande.EN_ATTENTE;
    }

    public void setPieceJustificative(PieceJustificative piece) {
        if (piece != null) piece.setDemande(this);
        this.pieceJustificative = piece;
    }
    
}