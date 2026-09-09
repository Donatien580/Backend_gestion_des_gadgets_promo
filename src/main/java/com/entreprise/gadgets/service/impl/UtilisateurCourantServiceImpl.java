package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.repository.UtilisateurRepository;
import com.entreprise.gadgets.service.UtilisateurCourantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurCourantServiceImpl implements UtilisateurCourantService {

    private static final String EMAIL_UTILISATEUR_SYSTEME = "systeme@dcm.bf";

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public Utilisateur obtenirUtilisateurConnecte() {
        return utilisateurRepository.findByEmail(EMAIL_UTILISATEUR_SYSTEME)
            .orElseThrow(() -> new IllegalStateException(
                "Le compte technique \"" + EMAIL_UTILISATEUR_SYSTEME
                    + "\" est introuvable : vérifier que la migration V2 a bien été exécutée."));
    }
}
