package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.service.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @GetMapping("/stock")
    public ResponseEntity<byte[]> genererRapportStock(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        byte[] pdfBytes = rapportService.genererRapportStockPdf(dateDebut, dateFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("rapport-stock.pdf")
                .build());
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}