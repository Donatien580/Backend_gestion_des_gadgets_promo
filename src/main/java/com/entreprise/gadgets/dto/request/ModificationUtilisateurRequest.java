package com.entreprise.gadgets.dto.request;

import com.entreprise.gadgets.model.enums.RoleType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModificationUtilisateurRequest(
	@NotBlank(message = "Le nom est obligatoire.")
	String nom,
	
	@NotBlank(message = "Le prénom est obligatoire.")
	String prenom,
	
	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "L'email n'est pas valide.")
	String email,
	
	@NotNull(message = "Le rôle est obligatoire.")
	RoleType role,
	
	@NotNull(message = "Le statut actif/inactif est obligatoire.")
	Boolean actif
) {}
