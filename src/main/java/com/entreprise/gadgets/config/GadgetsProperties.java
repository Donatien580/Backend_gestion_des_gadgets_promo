package com.entreprise.gadgets.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix="app.gadgets")
@Getter
@Setter
public class GadgetsProperties {
   private int seuilAlerteDefaut=50;
   private int margeAvertissementPourcentage=20;
}
