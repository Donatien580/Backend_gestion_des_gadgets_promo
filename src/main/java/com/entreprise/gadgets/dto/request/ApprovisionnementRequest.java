package com.entreprise.gadgets.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record ApprovisionnementRequest(

    /** Optionnelle : si absente, la date/heure de saisie est utilisée. */
    LocalDateTime dateReception,

    @NotBlank(message = "Le fournisseur est obligatoire.")
    String fournisseur,

    String numeroPV,

    String observations,

    @NotEmpty(message = "Un approvisionnement doit contenir au moins une ligne.")
    @Valid
    List<LigneApprovisionnementRequest> lignes
) {}
