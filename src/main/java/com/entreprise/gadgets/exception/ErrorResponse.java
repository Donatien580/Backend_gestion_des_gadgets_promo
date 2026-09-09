package com.entreprise.gadgets.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Format uniforme de toute réponse d'erreur retournée par l'API.
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String code,
    String message,
    String path,
    List<String> details
) {
    public ErrorResponse(int status, String code, String message, String path) {
        this(LocalDateTime.now(), status, code, message, path, List.of());
    }
    
    public ErrorResponse(int status, String code, String message, String path, List<String> details) {
        this(LocalDateTime.now(), status, code, message, path, details);
    }
}
