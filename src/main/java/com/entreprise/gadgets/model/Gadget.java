package com.entreprise.gadgets.model;

import com.entreprise.gadgets.exception.StockInsuffisantException;
import com.entreprise.gadgets.model.enums.EtatGadget;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gadget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gadget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gadget")
    private Integer idGadget;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "designation", length = 30)
    private String designation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "seuil_alerte", nullable = false)
    @Builder.Default
    private Integer seuilAlerte = 50;

    @Column(name = "photo_gadget", length = 255)
    private String photoGadget;

    @Column(name = "quantite_disponible", nullable = false)
    @Builder.Default
    private Integer quantiteDisponible = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", length = 30)
    @Builder.Default
    private EtatGadget etat = EtatGadget.DISPONIBLE;

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name="alerte_avertissement_envoyee", nullable = false)
    @Builder.Default
    private Boolean alerteAvertissementEnvoyee = false;
    
    @Column(name = "alerte_critique_envoyee", nullable = false)
    @Builder.Default
    private Boolean alerteCritiqueEnvoyee = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie", nullable = false)
    private Categorie categorie;

    /**
     * Augmente le stock disponible (utilisé uniquement par StockService).
     */
    public void augmenterStock(Integer quantite) {
        this.quantiteDisponible += quantite;
    }

    /**
     * Diminue le stock disponible en garantissant qu'il ne devienne jamais négatif.
     */
    public void diminuerStock(Integer quantite) {
        if (this.quantiteDisponible < quantite) {
            throw new StockInsuffisantException(
                "Stock insuffisant pour \"" + this.libelle + "\" - disponible : "
                    + this.quantiteDisponible + ", demandé : " + quantite);
        }
        this.quantiteDisponible -= quantite;
    }

    /**
     * Indique si le stock actuel est descendu au niveau (ou sous) le seuil d'alerte.
     */
    public boolean estSousSeuilAlerte() {
        return this.quantiteDisponible <= this.seuilAlerte;
    }
    
    public void reinitialiserAlertes() {
        this.alerteAvertissementEnvoyee = false;
        this.alerteCritiqueEnvoyee = false;
    }
}
