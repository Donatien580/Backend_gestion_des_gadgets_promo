package com.entreprise.gadgets.exception;

/** Levée quand une ressource demandée (par id) n'existe pas en base. */
public class RessourceIntrouvableException extends BusinessException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }

    public static RessourceIntrouvableException pour(String entite, Object id) {
        return new RessourceIntrouvableException(entite + " introuvable (id=" + id + ")");
    }
}
