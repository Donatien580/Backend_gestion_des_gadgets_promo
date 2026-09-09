package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.EtatInventaire;

import com.entreprise.gadgets.model.enums.TypeInventaire;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "inventaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventaire")
    private Integer idInventaire;

    @Column(name = "date_inventaire", nullable = false)
    private LocalDate dateInventaire;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_inventaire", nullable = false, length = 20)
    private TypeInventaire typeInventaire;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    @Builder.Default
    private EtatInventaire etat = EtatInventaire.EN_COURS;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @CreationTimestamp
    @Column(name="date_creation", updatable=false)
    private LocalDateTime dateCreation;
   
    @UpdateTimestamp
    @Column(name="date_modification")
    private LocalDateTime dateModification;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur realisateur;

    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneInventaire> lignes = new ArrayList<>();
    
    public void ajouterLigne(LigneInventaire ligne) {
    	lignes.add(ligne);
    	ligne.setInventaire(this);
    }
}
