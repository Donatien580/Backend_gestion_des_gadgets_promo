package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.AffectationDemandeRequest;
import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.request.RefusDemandeRequest;
import com.entreprise.gadgets.dto.response.AgentResume;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demandes")
@RequiredArgsConstructor
public class DemandeController {

	private final DemandeService demandeService;

    @GetMapping
    public PageResponse<DemandeResponse> lister(
        @RequestParam(required = false) EtatDemande etat,
        @RequestParam(required = false) String recherche,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return demandeService.lister(etat, recherche, page, size);
    }

    @GetMapping("/agents-affectables")
    public List<AgentResume> listerAgentsAffectables() {
        return demandeService.listerAgentsAffectables();
    }

    @GetMapping("/{id}")
    public DemandeResponse obtenir(@PathVariable Integer id) {
        return demandeService.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('AGENT_SAISIE','CHEF_SERVICE','CHEF_DEPARTEMENT','ADMIN')")
    public DemandeResponse creer(@Valid @RequestBody DemandeRequest requete) {
        return demandeService.creer(requete);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT_SAISIE','CHEF_SERVICE','CHEF_DEPARTEMENT','ADMIN')")
    public DemandeResponse modifier(@PathVariable Integer id, @Valid @RequestBody DemandeRequest requete) {
        return demandeService.modifier(id, requete);
    }

    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('CHEF_DEPARTEMENT','CHEF_SERVICE','ADMIN')")
    public DemandeResponse valider(@PathVariable Integer id) {
        return demandeService.valider(id);
    }

    @PatchMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('CHEF_DEPARTEMENT','CHEF_SERVICE','ADMIN')")
    public DemandeResponse refuser(@PathVariable Integer id, @Valid @RequestBody RefusDemandeRequest requete) {
        return demandeService.refuser(id, requete);
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('AGENT_SAISIE','CHEF_SERVICE','CHEF_DEPARTEMENT','ADMIN')")
    public DemandeResponse annuler(@PathVariable Integer id) {
        return demandeService.annuler(id);
    }

    @PatchMapping("/{id}/affecter")
    @PreAuthorize("hasAnyRole('CHEF_DEPARTEMENT','ADMIN')")
    public DemandeResponse affecter(@PathVariable Integer id, @Valid @RequestBody AffectationDemandeRequest requete) {
        return demandeService.affecter(id, requete);
    }

    @PostMapping("/{id}/piece-justificative")
    @PreAuthorize("hasAnyRole('AGENT_SAISIE','CHEF_SERVICE','CHEF_DEPARTEMENT','ADMIN')")
    public DemandeResponse uploaderPieceJustificative(
        @PathVariable Integer id, @RequestParam("fichier") MultipartFile fichier
    ) {
        return demandeService.uploaderPieceJustificative(id, fichier);
    }
    
    @GetMapping("/suggestions/noms")
    public List<String> suggererNoms(@RequestParam(defaultValue = "") String prefixe) {
        return demandeService.suggererNoms(prefixe);
    }

    @GetMapping("/suggestions/prenoms")
    public List<String> suggererPrenoms(@RequestParam(defaultValue = "") String prefixe) {
        return demandeService.suggererPrenoms(prefixe);
    }

    @GetMapping("/suggestions/services")
    public List<String> suggererServices(@RequestParam(defaultValue = "") String prefixe) {
        return demandeService.suggererServices(prefixe);
    }

    @GetMapping("/suggestions/structures")
    public List<String> suggererStructures(@RequestParam(defaultValue = "") String prefixe) {
        return demandeService.suggererStructures(prefixe);
    }
}
