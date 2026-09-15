package com.entreprise.gadgets.service;

import com.entreprise.gadgets.model.Gadget;

public interface NotificationSeuilService {
    /** À appeler après tout mouvement de stock affectant ce gadget. */
    void verifierSeuils(Gadget gadget);
}