package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "piece_justificative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_piece")
    private Integer idPiece;

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false, length = 500)
    private String cheminFichier;

    @Column(name = "type_fichier", length = 50)
    private String typeFichier;

    @Column(name = "taille")
    private Long taille;

    @CreationTimestamp
    @Column(name = "date_upload", updatable = false)
    private LocalDateTime dateUpload;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false, unique = true)
    private Demande demande;
    
    @PrePersist
    protected void onCreate() {
    	if(dateUpload == null) dateUpload = LocalDateTime.now();
    }
}
