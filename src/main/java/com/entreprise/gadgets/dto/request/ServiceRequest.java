package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ServiceRequest(
    @NotBlank(message = "Le libellé du service est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    String libelleService,

    @Size(max = 20, message = "Le code ne doit pas dépasser 20 caractères")
    String codeService,

    @Size(max = 20, message = "Le matricule ne doit pas dépasser 20 caractères")
    String matriculeResponsable,

    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    String nomResponsable,

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    String telephoneResponsable
) {}