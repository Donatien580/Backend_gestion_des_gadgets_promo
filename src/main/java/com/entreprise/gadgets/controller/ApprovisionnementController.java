package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.ApprovisionnementRequest;
import com.entreprise.gadgets.dto.request.CorrectionApprovisionnementRequest;
import com.entreprise.gadgets.dto.response.ApprovisionnementResponse;
import com.entreprise.gadgets.service.ApprovisionnementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/approvisionnements")
@RequiredArgsConstructor
public class ApprovisionnementController {

    private final ApprovisionnementService approvisionnementService;

    @GetMapping
    public Page<ApprovisionnementResponse> lister(
        @RequestParam(required = false) String recherche,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return approvisionnementService.lister(pageable, recherche);
    }

    @GetMapping("/{id}")
    public ApprovisionnementResponse obtenir(@PathVariable Integer id) {
        return approvisionnementService.obtenirParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('GESTIONNAIRE_STOCK','ADMIN')")
    public ApprovisionnementResponse creer(@Valid @RequestBody ApprovisionnementRequest requete) {
        return approvisionnementService.creer(requete);
    }
    
    @PatchMapping("/{id}/corriger")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE_STOCK','ADMIN')")
    public ApprovisionnementResponse corriger(
            @PathVariable Integer id,
            @Valid @RequestBody CorrectionApprovisionnementRequest requete
     ) {
            return approvisionnementService.corriger(id, requete);
     }
    
    @GetMapping("/suggestions/fournisseurs")
    public List<String> suggererFournisseurs(@RequestParam(defaultValue = "") String prefixe) {
        return approvisionnementService.suggererFournisseurs(prefixe);
    }
}
