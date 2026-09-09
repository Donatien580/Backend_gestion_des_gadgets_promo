package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.GadgetRequest;
import com.entreprise.gadgets.dto.request.GadgetStatutRequest;
import com.entreprise.gadgets.dto.response.GadgetResponse;
import com.entreprise.gadgets.service.GadgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// TODO (intégration Keycloak) : restreindre creer/modifier/changerStatut aux
// rôles ADMIN et GESTIONNAIRE_STOCK via @PreAuthorize une fois l'authentification
// branchée (cf. remarque dans CategorieController).
@RestController
@RequestMapping("/api/v1/gadgets")
@RequiredArgsConstructor
public class GadgetController {

    private final GadgetService gadgetService;

    @GetMapping
    public Page<GadgetResponse> lister(
        @RequestParam(required = false) Integer idCategorie,
        @RequestParam(defaultValue = "false") boolean inclureInactifs,
        @RequestParam(required = false) String recherche,
        @PageableDefault(size = 20, sort = "libelle") Pageable pageable
    ) {
        return gadgetService.lister(pageable, idCategorie, inclureInactifs, recherche);
    }

    @GetMapping("/alertes-stock")
    public List<GadgetResponse> listerSousSeuilAlerte() {
        return gadgetService.listerSousSeuilAlerte();
    }

    @GetMapping("/{id}")
    public GadgetResponse obtenir(@PathVariable Integer id) {
        return gadgetService.obtenirParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GadgetResponse creer(@Valid @RequestBody GadgetRequest requete) {
        return gadgetService.creer(requete);
    }

    @PutMapping("/{id}")
    public GadgetResponse modifier(@PathVariable Integer id, @Valid @RequestBody GadgetRequest requete) {
        return gadgetService.modifier(id, requete);
    }

    @PatchMapping("/{id}/statut")
    public GadgetResponse changerStatut(@PathVariable Integer id, @Valid @RequestBody GadgetStatutRequest requete) {
        return gadgetService.changerStatut(id, requete.actif());
    }

    @PostMapping("/{id}/photo")
    public GadgetResponse televerserPhoto(@PathVariable Integer id, @RequestParam("fichier") MultipartFile fichier) {
        return gadgetService.mettreAJourPhoto(id, fichier);
    }
}
