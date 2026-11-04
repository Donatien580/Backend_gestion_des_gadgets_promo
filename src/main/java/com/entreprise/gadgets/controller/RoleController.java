package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.response.RoleResponse;
import com.entreprise.gadgets.repository.RoleRepository;
import com.entreprise.gadgets.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    public List<RoleResponse> lister() {
        return roleRepository.findAll().stream()
            .map(role -> new RoleResponse(
                role.getIdRole(),
                role.getNom(),
                role.getDescription(),
                utilisateurRepository.countByRole_Nom(role.getNom())
            ))
            .toList();
    }
}