package com.entreprise.gadgets.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entreprise.gadgets.service.FichierStockageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fichiers")
@RequiredArgsConstructor
public class FichierController {
	
	private final FichierStockageService fichierStockageService;

    @GetMapping("/{*cheminRelatif}")
    public ResponseEntity<Resource> charger(@PathVariable String cheminRelatif) {
    	
    	if (cheminRelatif.startsWith("/")) {
            cheminRelatif = cheminRelatif.substring(1);
        }
    	
        Resource resource = fichierStockageService.charger(cheminRelatif);
        String contentType = determineContentType(cheminRelatif);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private String determineContentType(String chemin) {
        if (chemin.endsWith(".pdf")) return "application/pdf";
        if (chemin.endsWith(".jpg") || chemin.endsWith(".jpeg")) return "image/jpeg";
        if (chemin.endsWith(".png")) return "image/png";
        if (chemin.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }

}
