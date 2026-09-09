package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.GadgetRequest;
import com.entreprise.gadgets.dto.response.GadgetResponse;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.GadgetMapper;
import com.entreprise.gadgets.model.Categorie;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.enums.EtatGadget;
import com.entreprise.gadgets.repository.CategorieRepository;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.service.FichierStockageService;
import com.entreprise.gadgets.service.GadgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GadgetServiceImpl implements GadgetService {

    private final GadgetRepository gadgetRepository;
    private final CategorieRepository categorieRepository;
    private final GadgetMapper gadgetMapper;
    private final FichierStockageService fichierStockageService;

    @Override
    @Transactional(readOnly = true)
    public Page<GadgetResponse> lister(Pageable pageable, Integer idCategorie, boolean inclureInactifs, String recherche) {
        String rechercheNettoyee = (recherche == null || recherche.isBlank()) ? null : recherche.trim();
        return gadgetRepository.rechercher(idCategorie, inclureInactifs, rechercheNettoyee, pageable)
            .map(gadgetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GadgetResponse> listerSousSeuilAlerte() {
        return gadgetRepository.findGadgetsSousSeuilAlerte().stream()
            .map(gadgetMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GadgetResponse obtenirParId(Integer id) {
        return gadgetMapper.toResponse(trouverParId(id));
    }

    @Override
    @Transactional
    public GadgetResponse creer(GadgetRequest requete) {
        Categorie categorie = trouverCategorieParId(requete.idCategorie());

        Gadget gadget = Gadget.builder()
            .libelle(requete.libelle())
            .designation(requete.designation())
            .description(requete.description())
            .seuilAlerte(requete.seuilAlerte())
            .categorie(categorie)
            .quantiteDisponible(0) // Le stock initial entre uniquement via un approvisionnement.
            .etat(EtatGadget.DISPONIBLE)
            .actif(true)
            .build();

        return gadgetMapper.toResponse(gadgetRepository.save(gadget));
    }

    @Override
    @Transactional
    public GadgetResponse modifier(Integer id, GadgetRequest requete) {
        Gadget gadget = trouverParId(id);
        Categorie categorie = trouverCategorieParId(requete.idCategorie());

        gadget.setLibelle(requete.libelle());
        gadget.setDesignation(requete.designation());
        gadget.setDescription(requete.description());
        gadget.setSeuilAlerte(requete.seuilAlerte());
        gadget.setCategorie(categorie);
        // quantiteDisponible et etat ne sont volontairement pas touchés ici :
        // seul StockService est autorisé à les modifier.

        return gadgetMapper.toResponse(gadget);
    }

    @Override
    @Transactional
    public GadgetResponse changerStatut(Integer id, boolean actif) {
        Gadget gadget = trouverParId(id);
        gadget.setActif(actif);
        return gadgetMapper.toResponse(gadget);
    }

    @Override
    @Transactional
    public GadgetResponse mettreAJourPhoto(Integer id, MultipartFile fichier) {
        Gadget gadget = trouverParId(id);

        String ancienneCheminPhoto = gadget.getPhotoGadget();
        String nouveauCheminPhoto = fichierStockageService.enregistrer(fichier, "gadgets");
        gadget.setPhotoGadget(nouveauCheminPhoto);

        if (ancienneCheminPhoto != null) {
            fichierStockageService.supprimer(ancienneCheminPhoto);
        }

        return gadgetMapper.toResponse(gadget);
    }

    private Gadget trouverParId(Integer id) {
        return gadgetRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Gadget", id));
    }

    private Categorie trouverCategorieParId(Integer id) {
        return categorieRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Catégorie", id));
    }
}
