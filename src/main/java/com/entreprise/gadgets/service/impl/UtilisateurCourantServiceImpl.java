package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.model.Role;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.RoleType;
import com.entreprise.gadgets.repository.RoleRepository;
import com.entreprise.gadgets.repository.UtilisateurRepository;
import com.entreprise.gadgets.service.UtilisateurCourantService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilisateurCourantServiceImpl implements UtilisateurCourantService {

	/** Ordre de priorité si un utilisateur Keycloak porte plusieurs rôles applicatifs. */
    private static final List<RoleType> PRIORITE_ROLES = List.of(
        RoleType.ADMIN, RoleType.CHEF_DEPARTEMENT, RoleType.CHEF_SERVICE,
        RoleType.GESTIONNAIRE_STOCK, RoleType.AGENT_SAISIE
    );

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Utilisateur obtenirUtilisateurConnecte() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Aucun utilisateur authentifié dans le contexte de sécurité.");
        }

        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Le token Keycloak ne contient pas de claim \"email\".");
        }

        return utilisateurRepository.findByEmail(email)
            .orElseGet(() -> provisionnerUtilisateur(jwt, email, authentication.getAuthorities()));
    }

    /**
     * Provisionnement "just-in-time" : à la première connexion d'un utilisateur Keycloak
     * valide, on crée sa fiche locale (nécessaire pour les FK id_utilisateur sur
     * Demande/Approvisionnement/MouvementStock, etc.).
     */
    private Utilisateur provisionnerUtilisateur(Jwt jwt, String email,
                                                 Collection<? extends GrantedAuthority> autorites) {
        RoleType roleType = resoudreRolePrincipal(autorites);
        Role role = roleRepository.findByNom(roleType)
            .orElseThrow(() -> new IllegalStateException(
                "Le rôle \"" + roleType + "\" n'existe pas en base (table role) : vérifier les migrations."));

        String nomComplet = jwt.getClaimAsString("name");
        String prenom = jwt.getClaimAsString("given_name");
        String nom = jwt.getClaimAsString("family_name");
        if ((prenom == null || nom == null) && nomComplet != null) {
            String[] parties = nomComplet.trim().split("\\s+", 2);
            prenom = parties.length > 0 ? parties[0] : "";
            nom = parties.length > 1 ? parties[1] : "";
        }

        Utilisateur utilisateur = Utilisateur.builder()
            .nom(nom != null ? nom : email)
            .prenom(prenom != null ? prenom : "")
            .email(email)
            .actif(true)
            .role(role)
            .build();

        return utilisateurRepository.save(utilisateur);
    }

    private RoleType resoudreRolePrincipal(Collection<? extends GrantedAuthority> autorites) {
        List<String> noms = autorites.stream().map(GrantedAuthority::getAuthority).toList();
        return PRIORITE_ROLES.stream()
            .filter(role -> noms.contains("ROLE_" + role.name()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "Aucun rôle applicatif reconnu dans le token Keycloak (rôles reçus : " + noms + ")."));
    }
}
