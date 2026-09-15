package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.config.GadgetsProperties;
import com.entreprise.gadgets.dto.response.GadgetAlerteResponse;
import com.entreprise.gadgets.dto.response.NiveauStockGadgetResponse;
import com.entreprise.gadgets.dto.response.TableauDeBordResponse;
import com.entreprise.gadgets.mapper.DemandeMapper;
import com.entreprise.gadgets.model.Demande;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.repository.*;
import com.entreprise.gadgets.service.TableauDeBordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableauDeBordServiceImpl implements TableauDeBordService {

    private final GadgetRepository gadgetRepository;
    private final DemandeRepository demandeRepository;
    private final ApprovisionnementRepository approvisionnementRepository;
    private final DistributionRepository distributionRepository;
    private final DemandeMapper demandeMapper;
    private final GadgetsProperties gadgetsProperties;
    private final CategorieRepository categorieRepository;

    @Override
    @Transactional(readOnly = true)
    public TableauDeBordResponse obtenirTableauDeBord() {
        List<Gadget> gadgetsActifs = gadgetRepository.findByActifTrue();

        List<GadgetAlerteResponse> gadgetsAlertes = gadgetsActifs.stream()
            .map(this::toAlerteResponse)
            .filter(g -> g != null)
            .sorted(Comparator.comparing(GadgetAlerteResponse::quantiteDisponible))
            .toList();

        long critiques = gadgetsAlertes.stream().filter(g -> g.niveau().equals("CRITIQUE")).count();
        long avertissements = gadgetsAlertes.stream().filter(g -> g.niveau().equals("AVERTISSEMENT")).count();

        List<NiveauStockGadgetResponse> niveauxStockGadgets = gadgetRepository
            .findTop10ByActifTrueOrderByQuantiteDisponibleDesc()
            .stream()
            .map(g -> new NiveauStockGadgetResponse(
                g.getIdGadget(), g.getLibelle(), g.getCategorie().getLibelle(), g.getQuantiteDisponible()))
            .toList();

        LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        List<Demande> demandesRecentes = demandeRepository.findTop5ByOrderByDateDemandeDesc();

        return new TableauDeBordResponse(
            gadgetsActifs.size(),
            categorieRepository.count(),
            critiques,
            avertissements,
            demandeRepository.countByEtat(EtatDemande.EN_ATTENTE),
            demandeRepository.countByEtat(EtatDemande.AFFECTEE),
            approvisionnementRepository.countByDateReceptionAfter(debutMois),
            distributionRepository.countByDateDistributionAfter(debutMois),
            gadgetsAlertes,
            niveauxStockGadgets,
            demandesRecentes.stream().map(demandeMapper::toResponse).toList()
        );
    }

    private GadgetAlerteResponse toAlerteResponse(Gadget gadget) {
        int seuilAvertissement = gadget.getSeuilAlerte()
            + (gadget.getSeuilAlerte() * gadgetsProperties.getMargeAvertissementPourcentage() / 100);

        String niveau;
        if (gadget.getQuantiteDisponible() <= gadget.getSeuilAlerte()) {
            niveau = "CRITIQUE";
        } else if (gadget.getQuantiteDisponible() <= seuilAvertissement) {
            niveau = "AVERTISSEMENT";
        } else {
            return null; // stock sain : n'apparaît pas dans le widget
        }

        return new GadgetAlerteResponse(
            gadget.getIdGadget(), gadget.getLibelle(), gadget.getCategorie().getLibelle(),
            gadget.getQuantiteDisponible(), gadget.getSeuilAlerte(), niveau
        );
    }
}