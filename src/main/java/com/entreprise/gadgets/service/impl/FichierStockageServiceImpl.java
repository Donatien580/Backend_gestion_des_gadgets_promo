package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.exception.FichierInvalideException;

import com.entreprise.gadgets.service.FichierStockageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FichierStockageServiceImpl implements FichierStockageService {

    private static final Set<String> TYPES_AUTORISES = Set.of(
    	    "image/jpeg", "image/png", "image/webp", "application/pdf");
    
    @Value("${app.uploads.dossier}")
    private String dossierRacine;

    @Value("${app.uploads.taille-max-mo}")
    private long tailleMaxMo;

    @Override
    public String enregistrer(MultipartFile fichier, String sousDossier) {
        validerFichier(fichier);

        String extension = extensionDe(fichier.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + extension;

        try {
            Path dossierCible = Path.of(dossierRacine, sousDossier);
            Files.createDirectories(dossierCible);

            Path cheminComplet = dossierCible.resolve(nomFichier);
            Files.copy(fichier.getInputStream(), cheminComplet, StandardCopyOption.REPLACE_EXISTING);

            return sousDossier + "/" + nomFichier;
        } catch (IOException exception) {
            log.error("Échec de l'enregistrement du fichier {}", nomFichier, exception);
            throw new FichierInvalideException("Impossible d'enregistrer le fichier. Réessayez.");
        }
    }

    @Override
    public void supprimer(String cheminRelatif) {
        if (cheminRelatif == null || cheminRelatif.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(dossierRacine, cheminRelatif));
        } catch (IOException exception) {
            log.warn("Impossible de supprimer le fichier {} (ignoré)", cheminRelatif, exception);
        }
    }

    private void validerFichier(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new FichierInvalideException("Aucun fichier reçu.");
        }
        if (!TYPES_AUTORISES.contains(fichier.getContentType())) {
            throw new FichierInvalideException(
                "Format non supporté : seuls JPEG, PNG, WEBP et PDF sont acceptés.");
        }
        if (fichier.getSize() > tailleMaxMo * 1024 * 1024) {
            throw new FichierInvalideException("Fichier trop volumineux (max " + tailleMaxMo + " Mo).");
        }
    }
    
    @Override
    public Resource charger(String cheminRelatif) {
        try {
            Path racine = Path.of(dossierRacine).normalize().toAbsolutePath();
            Path chemin = racine.resolve(cheminRelatif).normalize();

            if (!chemin.startsWith(racine)) {
                throw new FichierInvalideException("Chemin de fichier invalide.");
            }

            Resource resource = new UrlResource(chemin.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Fichier introuvable : " + cheminRelatif);
            }
        } catch (MalformedURLException e) {
            throw new FichierInvalideException("Chemin de fichier invalide");
        } catch (FileNotFoundException e) {
            throw new FichierInvalideException("Fichier introuvable");
        }
    }
    private String extensionDe(String nomOriginal) {
        if (!StringUtils.hasText(nomOriginal) || !nomOriginal.contains(".")) {
            return "";
        }
        return nomOriginal.substring(nomOriginal.lastIndexOf('.'));
    }
}
