/*package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.DemandeExterneRequest;
import com.entreprise.gadgets.dto.request.DemandeInterneRequest;
import com.entreprise.gadgets.dto.request.RefusDemandeRequest;
import com.entreprise.gadgets.dto.request.TraitementDemandeRequest;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// TODO (intégration Keycloak) : restreindre valider/refuser/affecter aux
// rôles CHEF_DEPARTEMENT, traiter à CHEF_SERVICE (cf. remarque dans
// CategorieController). "affecter" s'auto-attribue à l'utilisateur courant
// en attendant une vraie sélection d'agent une fois les comptes réels actifs.
@RestController
@RequestMapping("/api/v1/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;

    @GetMapping
    public Page<DemandeResponse> lister(
        @RequestParam(required = false) EtatDemande etat,
        @RequestParam(required = false) String recherche,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return demandeService.lister(pageable, etat, recherche);
    }

    @GetMapping("/{id}")
    public DemandeResponse obtenir(@PathVariable Integer id) {
        return demandeService.obtenirParId(id);
    }

    @PostMapping("/internes")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeResponse creerInterne(@Valid @RequestBody DemandeInterneRequest requete) {
        return demandeService.creerInterne(requete);
    }

    @PostMapping("/externes")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeResponse creerExterne(@Valid @RequestBody DemandeExterneRequest requete) {
        return demandeService.creerExterne(requete);
    }

    @PatchMapping("/{id}/valider")
    public DemandeResponse valider(@PathVariable Integer id) {
        return demandeService.valider(id);
    }

    @PatchMapping("/{id}/refuser")
    public DemandeResponse refuser(@PathVariable Integer id, @Valid @RequestBody RefusDemandeRequest requete) {
        return demandeService.refuser(id, requete);
    }

    @PatchMapping("/{id}/annuler")
    public DemandeResponse annuler(@PathVariable Integer id) {
        return demandeService.annuler(id);
    }

    @PatchMapping("/{id}/affecter")
    public DemandeResponse affecter(@PathVariable Integer id) {
        return demandeService.affecter(id);
    }

    @PatchMapping("/{id}/traiter")
    public DemandeResponse traiter(@PathVariable Integer id, @Valid @RequestBody TraitementDemandeRequest requete) {
        return demandeService.traiter(id, requete);
    }

    @PostMapping("/{id}/piece-justificative")
    public DemandeResponse televerserPieceJustificative(
        @PathVariable Integer id, @RequestParam("fichier") MultipartFile fichier
    ) {
        return demandeService.mettreAJourPieceJustificative(id, fichier);
    }
}*/

package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.DemandeRequest;
import com.entreprise.gadgets.dto.response.DemandeResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.model.enums.EtatDemande;
import com.entreprise.gadgets.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            @RequestParam(defaultValue = "10") int size) {
        return demandeService.lister(etat, recherche, page, size);
    }
    
    @GetMapping("/{id}")
    public DemandeResponse obtenir(@PathVariable Integer id) {
        return demandeService.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeResponse creer(@Valid @RequestBody DemandeRequest requete) {
        return demandeService.creer(requete);
    }

    @PutMapping("/{id}")
    public DemandeResponse modifier(@PathVariable Integer id,
                                    @Valid @RequestBody DemandeRequest requete) {
        return demandeService.modifier(id, requete);
    }

    @PutMapping("/{id}/valider")
    public DemandeResponse valider(@PathVariable Integer id) {
        return demandeService.valider(id);
    }

    @PutMapping("/{id}/refuser")
    public DemandeResponse refuser(@PathVariable Integer id,
                                   @RequestParam String motif) {
        return demandeService.refuser(id, motif);
    }

    @PutMapping("/{id}/annuler")
    public DemandeResponse annuler(@PathVariable Integer id) {
        return demandeService.annuler(id);
    }

    /*@PutMapping("/{id}/affecter/{idAgent}")
    public DemandeResponse affecter(@PathVariable Integer id,
                                    @PathVariable Integer idAgent) {
        return demandeService.affecter(id, idAgent);
    }*/

    @PutMapping("/{id}/affecter")
    public DemandeResponse affecter(@PathVariable Integer id) {
        return demandeService.affecter(id);
    }
    
    @PutMapping("/{id}/traiter")
    public DemandeResponse traiter(@PathVariable Integer id,
                                   @RequestParam String decision,
                                   @RequestParam(required = false) String motifRefus) {
        return demandeService.traiter(id, decision, motifRefus);
    }

    @PostMapping("/{id}/piece-justificative")
    public DemandeResponse uploaderPieceJustificative(@PathVariable Integer id,
                                                      @RequestParam("fichier") MultipartFile fichier) {
        return demandeService.uploaderPieceJustificative(id, fichier);
    }
}
