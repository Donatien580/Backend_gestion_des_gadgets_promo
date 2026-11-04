package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.enums.RoleType;

public record RoleResponse(
		Integer idRole,
		RoleType nom,
		String description,
		long nombreUtilisateurs
) {}
