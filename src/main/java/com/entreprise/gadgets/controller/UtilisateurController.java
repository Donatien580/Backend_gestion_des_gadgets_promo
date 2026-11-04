package com.entreprise.gadgets.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.entreprise.gadgets.dto.request.ModificationUtilisateurRequest;
import com.entreprise.gadgets.dto.request.UtilisateurRequest;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.dto.response.UtilisateurResponse;
import com.entreprise.gadgets.service.UtilisateurService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UtilisateurController {
	private final UtilisateurService utilisateurService;

    @GetMapping
    public PageResponse<UtilisateurResponse> lister(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return utilisateurService.lister(page, size);
    }

    @GetMapping("/{id}")
    public UtilisateurResponse obtenir(@PathVariable Integer id) {
        return utilisateurService.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse creer(@Valid @RequestBody UtilisateurRequest requete) {
        return utilisateurService.creer(requete);
    }

    @PutMapping("/{id}")
    public UtilisateurResponse modifier(@PathVariable Integer id, @Valid @RequestBody ModificationUtilisateurRequest requete) {
        return utilisateurService.modifier(id, requete);
    }

    @PatchMapping("/{id}/statut")
    public UtilisateurResponse changerStatut(@PathVariable Integer id, @RequestParam boolean actif) {
        return utilisateurService.changerStatutActif(id, actif);
    }
}
