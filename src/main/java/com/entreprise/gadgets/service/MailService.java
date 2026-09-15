package com.entreprise.gadgets.service;

import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.TypeAlerteSeuil;

public interface MailService {
   void envoyerAlerteSeuil(Utilisateur destinataire, Gadget gadget, TypeAlerteSeuil type, int quantiteActuelle, int seuil);
}
