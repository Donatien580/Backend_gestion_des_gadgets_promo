package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PersonnelRequest(
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    String nom,

    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    String prenom,

    @Size(max = 20, message = "Le matricule ne doit pas dépasser 20 caractères")
    String matricule,

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    String telephone,

    @Size(max = 100, message = "La fonction ne doit pas dépasser 100 caractères")
    String fonction,

    Boolean actif,

    @NotNull(message = "Le service est obligatoire")
    Integer idService
) {}