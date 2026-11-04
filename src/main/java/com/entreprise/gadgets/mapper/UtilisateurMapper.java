package com.entreprise.gadgets.mapper;

import org.springframework.stereotype.Component;

import com.entreprise.gadgets.dto.response.UtilisateurResponse;
import com.entreprise.gadgets.model.Utilisateur;

@Component
public class UtilisateurMapper {

	public UtilisateurResponse toResponse(Utilisateur utilisateur) {
		return new UtilisateurResponse(
		  utilisateur.getIdUtilisateur(),
		  utilisateur.getNom(),
		  utilisateur.getPrenom(),
		  utilisateur.getEmail(),
          utilisateur.getActif(),
          utilisateur.getDateCreation(),
          utilisateur.getRole().getNom(),
          utilisateur.getRole().getDescription()
         );
	}
}
