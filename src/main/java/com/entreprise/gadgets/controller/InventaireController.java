package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.InventaireRequest;
import com.entreprise.gadgets.dto.request.JustificationRequest;
import com.entreprise.gadgets.dto.request.LigneInventaireRequest;
import com.entreprise.gadgets.dto.response.InventaireResponse;
import com.entreprise.gadgets.service.InventaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventaires")
@RequiredArgsConstructor
public class InventaireController {

    private final InventaireService inventaireService;

    @GetMapping
    public Page<InventaireResponse> lister(
        @PageableDefault(size = 20, sort = "dateInventaire") Pageable pageable
    ) {
        return inventaireService.lister(pageable);
    }

    @GetMapping("/{id}")
    public InventaireResponse obtenir(@PathVariable Integer id) {
        return inventaireService.obtenirParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventaireResponse creer(@Valid @RequestBody InventaireRequest requete) {
        return inventaireService.creer(requete);
    }

    @PostMapping("/{id}/lignes")
    public InventaireResponse saisirLigne(
        @PathVariable Integer id,
        @Valid @RequestBody LigneInventaireRequest requete
    ) {
        return inventaireService.saisirLigne(id, requete);
    }

    @PatchMapping("/{id}/lignes/{idLigne}/justification")
    public InventaireResponse justifierEcart(
        @PathVariable Integer id,
        @PathVariable Integer idLigne,
        @Valid @RequestBody JustificationRequest requete
    ) {
        return inventaireService.justifierEcart(id, idLigne, requete);
    }

    @PatchMapping("/{id}/terminer")
    public InventaireResponse terminer(@PathVariable Integer id) {
        return inventaireService.terminer(id);
    }

    @PatchMapping("/{id}/valider")
    public InventaireResponse valider(@PathVariable Integer id) {
        return inventaireService.valider(id);
    }
}