package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.model.Demande;

import com.entreprise.gadgets.model.Distribution;
import com.entreprise.gadgets.model.LigneDistribution;
import com.entreprise.gadgets.model.SignataireDistribution;
import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;
import com.entreprise.gadgets.dto.response.LigneDistributionResponse;
import com.entreprise.gadgets.dto.response.LigneGroupeResponse;
import com.entreprise.gadgets.dto.response.DistributionResponse;
import com.entreprise.gadgets.dto.response.GroupeBeneficiaireResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class DistributionMapper {
	
	public DistributionResponse toResponse(Distribution distribution) {
	    Demande demande = distribution.getDemande();

	    return new DistributionResponse(
	        distribution.getIdDistribution(),
	        distribution.getNumeroBordereau(),
	        distribution.getDateDistribution(),
	        distribution.getTypeDistribution(),
	        distribution.getEstDotation(),
	        distribution.getMotif(),
	        distribution.getDestinataire(),
	        distribution.getMatriculeReceptionnaire(),
	        distribution.getNomReceptionnaire(),
	        distribution.getPrenomReceptionnaire(),
	        distribution.getServiceReceptionnaire(),
	        distribution.getStructureBeneficiaire(),
	        distribution.getNombrePersonnes(),
	        distribution.getEtat(),
	        distribution.getDateGenerationBordereau(),
	        demande != null ? demande.getIdDemande() : null,
	        demande != null ? demande.getNumeroDemande() : null,
	        demande != null ? demande.getObjet() : null,
	        distribution.getLignes().stream().map(this::toLigneResponse).toList(),
	        construireGroupes(distribution)
	    );
	}

	private LigneDistributionResponse toLigneResponse(LigneDistribution ligne) {
	    return new LigneDistributionResponse(
	        ligne.getIdLigne(),
	        ligne.getGadget().getIdGadget(),
	        ligne.getGadget().getLibelle(),
	        ligne.getCategorieBeneficiaire(),
	        ligne.getNombreBeneficiaires(),
	        ligne.getQuantiteParPersonne(),
	        ligne.getQuantiteDistribuee(),
	        ligne.getActif(),
	        ligne.getMotifCorrection());
	}

	private List<GroupeBeneficiaireResponse> construireGroupes(Distribution distribution) {
	    Map<CategorieBeneficiaire, List<LigneDistribution>> parCategorie = new LinkedHashMap<>();
	    for (LigneDistribution l : distribution.getLignes()) {
	        if (Boolean.TRUE.equals(l.getActif())) {
	            parCategorie.computeIfAbsent(l.getCategorieBeneficiaire(), k -> new ArrayList<>()).add(l);
	        }
	    }
	    return parCategorie.entrySet().stream()
	        .map(e -> new GroupeBeneficiaireResponse(
	            e.getKey(),
	            e.getKey() != null ? e.getKey().getLibelle() : null,
	            e.getValue().get(0).getNombreBeneficiaires(),
	            distribution.getSignataires().stream()
	                .filter(s -> s.getCategorieBeneficiaire() == e.getKey())
	                .map(SignataireDistribution::getNomComplet)
	                .toList(),
	            e.getValue().stream()
	                .map(l -> new LigneGroupeResponse(
	                    l.getGadget().getIdGadget(), l.getGadget().getLibelle(),
	                    l.getQuantiteParPersonne(), l.getQuantiteDistribuee()))
	                .toList()))
	        .sorted(Comparator.comparingInt(g -> g.categorie() == null ? -1 : g.categorie().ordinal()))
	        .toList();
	}

}
