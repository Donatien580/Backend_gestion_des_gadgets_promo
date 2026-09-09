package com.entreprise.gadgets.exception;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RessourceIntrouvableException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "RESSOURCE_INTROUVABLE", ex.getMessage(), req);
    }

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<ErrorResponse> handleStock(StockInsuffisantException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "STOCK_INSUFFISANT", ex.getMessage(), req);
    }

    @ExceptionHandler(EtatInvalideException.class)
    public ResponseEntity<ErrorResponse> handleEtatInvalide(EtatInvalideException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "ETAT_INVALIDE", ex.getMessage(), req);
    }

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ErrorResponse> handleAccesRefuse(AccesRefuseException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "ACCES_REFUSE", ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "ACCES_REFUSE", "Droits insuffisants pour cette action.", req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "ERREUR_METIER", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
            .toList();
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(), "VALIDATION_ECHOUEE",
            "Certains champs sont invalides.", req.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Erreur inattendue sur {} : {}", req.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "ERREUR_INTERNE",
            "Une erreur est survenue. Contactez l'administrateur.", req);
    }
    
    @ExceptionHandler(FichierInvalideException.class)
    public ResponseEntity<ErrorResponse> handleFichierInvalide(FichierInvalideException ex, HttpServletRequest request) {
        log.warn("Fichier invalide ou introuvable : {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "FICHIER_INVALIDE",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(status.value(), code, message, req.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
