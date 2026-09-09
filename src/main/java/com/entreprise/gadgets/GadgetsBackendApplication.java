package com.entreprise.gadgets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application de gestion des gadgets promotionnels (DCM).
 */
@SpringBootApplication
public class GadgetsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GadgetsBackendApplication.class, args);
    }
}
