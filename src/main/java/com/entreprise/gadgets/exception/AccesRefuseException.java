package com.entreprise.gadgets.exception;

/** Levée par une règle métier de contrôle d'accès complémentaire à @PreAuthorize. */
public class AccesRefuseException extends BusinessException {

    public AccesRefuseException(String message) {
        super(message);
    }
}
