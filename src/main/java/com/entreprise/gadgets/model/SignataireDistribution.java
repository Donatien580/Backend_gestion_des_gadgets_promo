package com.entreprise.gadgets.model;

import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "signataire_distribution")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SignataireDistribution {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_signataire")
	private Integer idSignataire;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_distribution", nullable = false)
	private Distribution distribution;

	@Enumerated(EnumType.STRING)
	@Column(name = "categorie_beneficiaire", length = 30)
	private CategorieBeneficiaire categorieBeneficiaire;

	@Column(name = "nom_complet", nullable = false, length = 150)
	private String nomComplet;

	@Column(name = "ordre", nullable = false)
	private Integer ordre;

}
