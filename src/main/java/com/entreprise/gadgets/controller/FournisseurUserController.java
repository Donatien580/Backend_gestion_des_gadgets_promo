package com.entreprise.gadgets.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entreprise.gadgets.dto.response.UtilisateurResponse;
import com.entreprise.gadgets.mapper.UtilisateurMapper;
import com.entreprise.gadgets.service.UtilisateurCourantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fournisseurUser")
@RequiredArgsConstructor
public class FournisseurUserController {
	private final UtilisateurCourantService utilisateurCourantService;
    private final UtilisateurMapper utilisateurMapper;

    @GetMapping
    public UtilisateurResponse obtenirMonProfil() {
        return utilisateurMapper.toResponse(utilisateurCourantService.obtenirUtilisateurConnecte());
    }
}
