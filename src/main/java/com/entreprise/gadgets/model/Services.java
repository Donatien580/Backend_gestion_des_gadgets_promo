package com.entreprise.gadgets.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Integer idService;

    @Column(name = "libelle_service", nullable = false, length = 100)
    private String libelleService;

    @Column(name = "code_service", length = 20, unique = true)
    private String codeService;

    @Column(name = "matricule_responsable", length = 20)
    private String matriculeResponsable;

    @Column(name = "nom_responsable", length = 100)
    private String nomResponsable;

    @Column(name = "telephone_responsable", length = 20)
    private String telephoneResponsable;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default 
    private List<Personnel> personnels = new ArrayList<>();

    public void ajouterPersonnel(Personnel personnel) {
        personnel.setService(this);
        this.personnels.add(personnel);
    }

    public void retirerPersonnel(Personnel personnel) {
        personnel.setService(null);
        this.personnels.remove(personnel);
    }
}