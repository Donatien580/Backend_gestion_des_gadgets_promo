package com.entreprise.gadgets.exception;

/** Levée quand un fichier uploadé ne respecte pas les contraintes (type, taille). */
public class FichierInvalideException extends BusinessException {

    public FichierInvalideException(String message) {
        super(message);
    }
}
