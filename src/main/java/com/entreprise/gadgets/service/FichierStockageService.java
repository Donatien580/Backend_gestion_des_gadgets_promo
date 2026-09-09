package com.entreprise.gadgets.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FichierStockageService {

    String enregistrer(MultipartFile fichier, String sousDossier);
    void supprimer(String cheminRelatif);
    Resource charger(String cheminRelatif);
    
}
