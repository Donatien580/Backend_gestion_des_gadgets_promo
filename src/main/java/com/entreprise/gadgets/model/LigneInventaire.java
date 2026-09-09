package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ligne_inventaire",
       uniqueConstraints = @UniqueConstraint(columnNames= {"id_inventaire","id_gadget"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneInventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private Integer idLigne;

    @Column(name = "stock_theorique", nullable = false)
    private Integer stockTheorique;

    @Column(name = "stock_reel", nullable = false)
    private Integer stockReel;

    @Column(name = "ecart", nullable = false)
    private Integer ecart;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "validation_justif", nullable = false)
    @Builder.Default
    private Boolean validationJustif = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gadget", nullable = false)
    private Gadget gadget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventaire", nullable = false)
    private Inventaire inventaire;

    public void calculerEcart() {
        this.ecart = this.stockTheorique - this.stockReel;
    }
    
    public boolean necessiteJustification() {
    	return this.ecart != null && this.ecart !=0;
    }
}
