package com.entreprise.gadgets.dto.request;

import com.entreprise.gadgets.model.enums.RoleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UtilisateurRequest(
   @NotBlank(message = "Le nom est obligatoire.")
   String nom,
   
   @NotBlank(message = "Le prenom est obligatoire.")
   String prenom,
   
   @NotBlank(message = "L'email est obligatoire.")
   String email,
   
   @NotNull(message = "Le rôle est obligatoire.")
   RoleType role
) {}
