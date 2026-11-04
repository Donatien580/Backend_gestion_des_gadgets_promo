package com.entreprise.gadgets.dto.response;

import java.time.LocalDateTime;

import com.entreprise.gadgets.model.enums.RoleType;

public record UtilisateurResponse(
   Integer idUtilisateur,
   String nom,
   String prenom,
   String email,
   Boolean actif,
   LocalDateTime dateCreation,
   RoleType role,
   String descriptionRole
) {}
