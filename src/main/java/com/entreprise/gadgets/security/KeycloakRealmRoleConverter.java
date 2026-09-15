package com.entreprise.gadgets.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convertit le claim "realm_access.roles" du token Keycloak en autorités Spring Security.
 * Les rôles Keycloak sont nommés directement "ROLE_XXX" (voir realm-export.json), donc on
 * les reprend tels quels — pas de double préfixage — pour que hasRole("XXX") fonctionne.
 */
@Component
public class KeycloakRealmRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> autorites = extraireRoles(jwt).stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        return new JwtAuthenticationToken(jwt, autorites, jwt.getClaimAsString("preferred_username"));
    }

    @SuppressWarnings("unchecked")
    private List<String> extraireRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return List.of();
        Object roles = realmAccess.get("roles");
        return roles instanceof List<?> liste ? (List<String>) liste : List.of();
    }
}
