package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.DistributionRequest;
import com.entreprise.gadgets.dto.response.DistributionResponse;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.service.DistributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/distributions")
@RequiredArgsConstructor
public class DistributionController {

    private final DistributionService distributionService;

    @GetMapping
    public PageResponse<DistributionResponse> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return distributionService.lister(page, size);
    }

    @GetMapping("/{id}")
    public DistributionResponse obtenir(@PathVariable Integer id) {
        return distributionService.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('GESTIONNAIRE_STOCK','ADMIN')")
    public DistributionResponse creer(@Valid @RequestBody DistributionRequest requete) {
        return distributionService.creer(requete);
    }

    @PatchMapping("/{id}/executer")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE_STOCK','CHEF_SERVICE','CHEF_DEPARTEMENT','ADMIN')")
    public DistributionResponse executer(@PathVariable Integer id) {
        return distributionService.executer(id);
    }

    @GetMapping("/{id}/bordereau-pdf")
    public ResponseEntity<byte[]> genererBordereauPdf(@PathVariable Integer id) {
        byte[] pdfBytes = distributionService.genererBordereauPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("bordereau-" + id + ".pdf")
                .build());
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/suggestions/noms-receptionnaire")
    public List<String> suggererNomsReceptionnaire(@RequestParam(defaultValue = "") String prefixe) {
        return distributionService.suggererNomsReceptionnaire(prefixe);
    }

    @GetMapping("/suggestions/prenoms-receptionnaire")
    public List<String> suggererPrenomsReceptionnaire(@RequestParam(defaultValue = "") String prefixe) {
        return distributionService.suggererPrenomsReceptionnaire(prefixe);
    }

    @GetMapping("/suggestions/services-receptionnaire")
    public List<String> suggererServicesReceptionnaire(@RequestParam(defaultValue = "") String prefixe) {
        return distributionService.suggererServicesReceptionnaire(prefixe);
    }

    @GetMapping("/suggestions/destinataires")
    public List<String> suggererDestinataires(@RequestParam(defaultValue = "") String prefixe) {
        return distributionService.suggererDestinataires(prefixe);
    }
}