package com.entreprise.gadgets.dto.request;

import com.entreprise.gadgets.model.enums.TypeDemande;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DemandeRequest(

    @NotBlank(message = "L'objet est obligatoire.")
    String objet,

    @NotNull(message = "Le type de demande est obligatoire.")
    TypeDemande typeDemande,

    LocalDate dateSouhaitee,
    String observations,

    @NotBlank(message = "Le nom du demandeur est obligatoire.")
    String nomDemandeur,

    String prenomDemandeur,
    String telephoneDemandeur,

    /* Demande interne */
    String matriculeDemandeur,
    String serviceDemandeur,

    /* Demande externe */
    String structureDemandeur
) {}