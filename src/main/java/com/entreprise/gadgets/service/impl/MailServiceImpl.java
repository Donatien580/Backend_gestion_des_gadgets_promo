package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.TypeAlerteSeuil;
import com.entreprise.gadgets.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String expediteur;

    @Override
    public void envoyerAlerteSeuil(Utilisateur destinataire, Gadget gadget, TypeAlerteSeuil type,
                                    int quantiteActuelle, int seuil) {
        if (destinataire.getEmail() == null || destinataire.getEmail().isBlank()) {
            return;
        }

        String objet = type == TypeAlerteSeuil.CRITIQUE
            ? "⚠️ Seuil de stock atteint — " + gadget.getLibelle()
            : "🔔 Stock bientôt sous le seuil — " + gadget.getLibelle();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(destinataire.getEmail());
            if (expediteur != null && !expediteur.isBlank()) {
                helper.setFrom(expediteur);
            }
            helper.setSubject(objet);
            helper.setText(construireCorpsHtml(destinataire, gadget, type, quantiteActuelle, seuil), true);
            mailSender.send(message);
            log.info("Alerte seuil ({}) envoyée à {} pour \"{}\"", type, destinataire.getEmail(), gadget.getLibelle());
        } catch (MessagingException e) {
            log.error("Échec de l'envoi de l'e-mail d'alerte seuil à {}", destinataire.getEmail(), e);
        }
    }

    private String construireCorpsHtml(Utilisateur destinataire, Gadget gadget, TypeAlerteSeuil type,
                                        int quantiteActuelle, int seuil) {
        String couleur = type == TypeAlerteSeuil.CRITIQUE ? "#dc2626" : "#a16207";
        String titre = type == TypeAlerteSeuil.CRITIQUE
            ? "Le seuil d'alerte est atteint"
            : "Le stock approche du seuil d'alerte";
        String message = type == TypeAlerteSeuil.CRITIQUE
            ? "La quantité disponible est descendue au niveau ou en dessous du seuil défini. Un réapprovisionnement est nécessaire."
            : "La quantité disponible se rapproche du seuil défini. Anticipez un réapprovisionnement.";

        return """
            <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
              <div style="border-left: 4px solid %s; padding: 16px; background: #f9fafb;">
                <h2 style="color: %s; margin-top: 0;">%s</h2>
                <p>Bonjour %s,</p>
                <p>%s</p>
                <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                  <tr><td style="padding: 4px 0; color: #6b7280;">Gadget</td><td style="padding: 4px 0; font-weight: bold;">%s</td></tr>
                  <tr><td style="padding: 4px 0; color: #6b7280;">Quantité disponible</td><td style="padding: 4px 0; font-weight: bold;">%d</td></tr>
                  <tr><td style="padding: 4px 0; color: #6b7280;">Seuil d'alerte</td><td style="padding: 4px 0; font-weight: bold;">%d</td></tr>
                </table>
                <p style="color: #6b7280; font-size: 0.85rem;">Notification automatique — GestStock Gadgets, SONABEL/DCM.</p>
              </div>
            </div>
            """.formatted(couleur, couleur, titre, destinataire.getPrenom(), message,
                gadget.getLibelle(), quantiteActuelle, seuil);
    }
}
