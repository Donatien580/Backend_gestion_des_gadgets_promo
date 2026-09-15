package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.config.GadgetsProperties;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.RoleType;
import com.entreprise.gadgets.model.enums.TypeAlerteSeuil;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.repository.UtilisateurRepository;
import com.entreprise.gadgets.service.MailService;
import com.entreprise.gadgets.service.NotificationSeuilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSeuilServiceImpl implements NotificationSeuilService {

    private static final List<RoleType> DESTINATAIRES_ALERTES = List.of(RoleType.GESTIONNAIRE_STOCK, RoleType.ADMIN);

    private final GadgetsProperties gadgetsProperties;
    private final GadgetRepository gadgetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public void verifierSeuils(Gadget gadget) {
        int quantite = gadget.getQuantiteDisponible();
        int seuil = gadget.getSeuilAlerte();
        int seuilAvertissement = seuil + (seuil * gadgetsProperties.getMargeAvertissementPourcentage() / 100);

        if (quantite <= seuil) {
            if (!Boolean.TRUE.equals(gadget.getAlerteCritiqueEnvoyee())) {
                envoyerAuxGestionnaires(gadget, TypeAlerteSeuil.CRITIQUE, quantite, seuil);
                gadget.setAlerteCritiqueEnvoyee(true);
                gadget.setAlerteAvertissementEnvoyee(true); // le critique implique l'avertissement
                gadgetRepository.save(gadget);
            }
        } else if (quantite <= seuilAvertissement) {
            if (!Boolean.TRUE.equals(gadget.getAlerteAvertissementEnvoyee())) {
                envoyerAuxGestionnaires(gadget, TypeAlerteSeuil.AVERTISSEMENT, quantite, seuil);
                gadget.setAlerteAvertissementEnvoyee(true);
                gadgetRepository.save(gadget);
            }
        } else if (Boolean.TRUE.equals(gadget.getAlerteAvertissementEnvoyee())
                || Boolean.TRUE.equals(gadget.getAlerteCritiqueEnvoyee())) {
            // Stock reconstitué au-delà de la marge : on réarme les deux alertes
            gadget.reinitialiserAlertes();
            gadgetRepository.save(gadget);
        }
    }

    private void envoyerAuxGestionnaires(Gadget gadget, TypeAlerteSeuil type, int quantite, int seuil) {
        List<Utilisateur> destinataires = utilisateurRepository
            .findByRole_NomInAndActifTrueOrderByNomAsc(DESTINATAIRES_ALERTES);

        for (Utilisateur destinataire : destinataires) {
            try {
                mailService.envoyerAlerteSeuil(destinataire, gadget, type, quantite, seuil);
            } catch (Exception e) {
                log.error("Échec de l'envoi de l'alerte seuil à {}", destinataire.getEmail(), e);
            }
        }
    }
}