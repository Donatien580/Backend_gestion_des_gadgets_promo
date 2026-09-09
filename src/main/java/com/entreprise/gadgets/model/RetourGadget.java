package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "retour_gadget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetourGadget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retour")
    private Integer idRetour;

    @CreationTimestamp
    @Column(name = "date_retour", updatable = false)
    private LocalDateTime dateRetour;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gadget", nullable = false)
    private Gadget gadget;

    /** Distribution d'origine dont ce retour provient (peut être nul si retour hors distribution). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_distribution")
    private Distribution distribution;
}
