package com.entreprise.gadgets.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Expose le dossier de fichiers uploadés (photos de gadgets...) sous /uploads/**. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.dossier}")
    private String dossierRacine;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + dossierRacine + "/");
    }
}
