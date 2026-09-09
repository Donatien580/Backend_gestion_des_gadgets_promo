package com.entreprise.gadgets.exception;

/** Levée quand une transition d'état métier n'est pas autorisée (ex: valider une demande déjà refusée). */
public class EtatInvalideException extends BusinessException {

    public EtatInvalideException(String message) {
        super(message);
    }
}
