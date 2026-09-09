package com.entreprise.gadgets.dto.request;

import com.entreprise.gadgets.model.enums.TypeDemande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record DemandeRequest(

    @NotBlank(message = "L'objet est obligatoire")
    String objet,

    @NotNull(message = "Le type de demande est obligatoire")
    TypeDemande typeDemande,

    LocalDate dateSouhaitee,
    String observations,

    /*
     * Demande interne
     */
    Integer idService,
    Integer nombrePersonnelsImpactes,
    
    /*
     * Demande externe
     */
    String structure,
    String representant,
    String telephone,

    @Valid
    @NotNull(message = "La liste des lignes est obligatoire")
    List<LigneDemandeRequest> lignes
) {
}