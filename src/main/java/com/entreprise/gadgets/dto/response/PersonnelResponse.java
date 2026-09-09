package com.entreprise.gadgets.dto.response;

import com.entreprise.gadgets.model.Services;

public record PersonnelResponse(
  Integer idPersonnel,
  String nom,
  String prenom,
  String matricule,
  String telephone,
  String fonction,
  Boolean actif,
  Integer idservice,
  String libelleService
) {}
