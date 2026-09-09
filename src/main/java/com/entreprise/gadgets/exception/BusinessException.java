package com.entreprise.gadgets.exception;

/**
 * Classe mère de toutes les exceptions métier de l'application.
 * Interceptée par GlobalExceptionHandler pour produire une réponse HTTP cohérente.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
