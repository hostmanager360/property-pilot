package com.propertypilot.registration_service.exception;

import com.propertypilot.registration_service.model.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------
    // 1000 — VALIDAZIONE
    // -------------------------

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseHandler<?>> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(1001, ex.getMessage()));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidEmail(InvalidEmailException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(1002, ex.getMessage()));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ResponseHandler<?>> handlePasswordMismatch(PasswordMismatchException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(1003, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(1004, "JSON non valido"));
    }

    @ExceptionHandler(InvalidTenantException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidTenant(InvalidTenantException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(1005, ex.getMessage()));
    }

    // -------------------------
    // 2000 — EMAIL
    // -------------------------

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ResponseHandler<?>> handleEmailSend(EmailSendException ex) {
        return ResponseEntity.status(500).body(ResponseHandler.error(2001, ex.getMessage()));
    }

    // -------------------------
    // 3000 — TOKEN
    // -------------------------

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(3001, ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ResponseHandler<?>> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(3002, ex.getMessage()));
    }

    // -------------------------
    // 4000 — PERMESSI / RUOLI
    // -------------------------

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseHandler<?>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(4001, ex.getMessage()));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(4002, ex.getMessage()));
    }

    @ExceptionHandler(StepNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleStepNotFound(StepNotFoundException ex) {
        return ResponseEntity.badRequest().body(ResponseHandler.error(4003, ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseHandler<?>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(4004, "Accesso negato"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseHandler<?>> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseHandler.error(4005, "Autenticazione richiesta"));
    }

    // -------------------------
    // 9999 — GENERICO
    // -------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHandler<?>> handleGeneric(Exception ex) {
        return ResponseEntity.status(500)
                .body(ResponseHandler.error(9999, "Errore interno del server"));
    }
}