package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.StatutIncident;
import com.entreprise.gadgets.model.enums.TypeIncident;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident")
    private Integer idIncident;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_incident", nullable = false, length = 30)
    private TypeIncident typeIncident;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "date_signalement", updatable = false)
    private LocalDateTime dateSignalement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private StatutIncident statut = StatutIncident.EN_COURS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_approvisionnement", nullable = false)
    private Approvisionnement approvisionnement;
}
