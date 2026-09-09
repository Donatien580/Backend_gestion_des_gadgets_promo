/*package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.TypeDemande;

@Entity
@Table(name = "demande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 30)
    private EtatDemande etat;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    // Champs pour demande interne
    @Column(name = "service", length = 100)
    private String service;

    @Column(name = "matricule_responsable", length = 20)
    private String matriculeResponsable;

    @Column(name = "nom_responsable", length = 100)
    private String nomResponsable;

    // Champs pour demande externe
    @Column(name = "structure", length = 100)
    private String structure;

    @Column(name = "representant", length = 100)
    private String representant;

    @Column(name = "telephone", length = 20)
    private String telephone;

    // ========== AJOUT : Agent de saisie ==========
    // L'utilisateur qui crée la demande (obligatoire selon la doc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur agentSaisie;

    // ========== AJOUT : Pièce justificative ==========
    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private PieceJustificative pieceJustificative;

    // Chef Service affecté au traitement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent_affecte")
    private Utilisateur agentAffecte;

    // Lignes de la demande
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneDemande> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dateDemande == null) dateDemande = LocalDateTime.now();
        if (etat == null) etat = EtatDemande.EN_ATTENTE;
    }

    public void ajouterLigne(LigneDemande ligne) {
        ligne.setDemande(this);
        this.lignes.add(ligne);
    }

    public void setPieceJustificative(PieceJustificative piece) {
        if (piece != null) piece.setDemande(this);
        this.pieceJustificative = piece;
    }

}*/

package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.model.enums.TypeDemande;

@Entity
@Table(name = "demande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 30)
    private EtatDemande etat;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    // Champs pour demande interne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    private Services service;

    @Column(name = "nombre_personnels_impactes")
    private Integer nombrePersonnelsImpactes;

    // Champs pour demande externe
    @Column(name = "structure", length = 100)
    private String structure;

    @Column(name = "representant", length = 100)
    private String representant;

    @Column(name = "telephone", length = 20)
    private String telephone;

    // Utilisateur qui a créé la demande (Agent de saisie)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur agentSaisie;

    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private PieceJustificative pieceJustificative;

    // Chef Service affecté au traitement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent_affecte")
    private Utilisateur agentAffecte;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneDemande> lignes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dateDemande == null) dateDemande = LocalDateTime.now();
        if (etat == null) etat = EtatDemande.EN_ATTENTE;
    }

    public void ajouterLigne(LigneDemande ligne) {
        ligne.setDemande(this);
        this.lignes.add(ligne);
    }

    public void setPieceJustificative(PieceJustificative piece) {
        if (piece != null) piece.setDemande(this);
        this.pieceJustificative = piece;
    }
}
