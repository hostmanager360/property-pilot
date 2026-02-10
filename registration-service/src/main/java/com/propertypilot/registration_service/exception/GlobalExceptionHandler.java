package com.propertypilot.registration_service.exception;

import com.propertypilot.registration_service.model.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // -------------------------
    // 1000 — VALIDAZIONE
    // -------------------------

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseHandler<?>> handleEmailExists(EmailAlreadyExistsException ex) {
        log.warn("Email già registrata: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.EMAIL_ALREADY_EXISTS.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidEmail(InvalidEmailException ex) {
        log.warn("Email non valida: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.INVALID_EMAIL.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ResponseHandler<?>> handlePasswordMismatch(PasswordMismatchException ex) {
        log.warn("Password mismatch: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.PASSWORD_MISMATCH.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidJson(HttpMessageNotReadableException ex) {
        log.warn("JSON non valido", ex);
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.INVALID_JSON.getCode(), ErrorCode.INVALID_JSON.getDefaultMessage()));
    }

    @ExceptionHandler(InvalidTenantException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidTenant(InvalidTenantException ex) {
        log.warn("Tenant non valido: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.INVALID_TENANT.getCode(), ex.getMessage()));
    }

    // -------------------------
    // 2000 — EMAIL
    // -------------------------

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ResponseHandler<?>> handleEmailSend(EmailSendException ex) {
        log.error("Errore invio email: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseHandler.error(ErrorCode.EMAIL_SEND_ERROR.getCode(), ex.getMessage()));
    }

    // -------------------------
    // 3000 — TOKEN
    // -------------------------

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTokenNotFound(TokenNotFoundException ex) {
        log.warn("Token non trovato: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.TOKEN_NOT_FOUND.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ResponseHandler<?>> handleTokenExpired(TokenExpiredException ex) {
        log.warn("Token scaduto: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.TOKEN_EXPIRED.getCode(), ex.getMessage()));
    }

    // -------------------------
    // 4000 — PERMESSI / RUOLI
    // -------------------------

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseHandler<?>> handleForbidden(ForbiddenException ex) {
        log.warn("Accesso negato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(ErrorCode.FORBIDDEN.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleRoleNotFound(RoleNotFoundException ex) {
        log.warn("Ruolo non trovato: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.ROLE_NOT_FOUND.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(StepNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleStepNotFound(StepNotFoundException ex) {
        log.warn("Step non trovato: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(ErrorCode.STEP_NOT_FOUND.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseHandler<?>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Accesso negato (Spring Security): {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getDefaultMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseHandler<?>> handleAuth(AuthenticationException ex) {
        log.warn("Autenticazione fallita: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseHandler.error(ErrorCode.AUTH_REQUIRED.getCode(), ErrorCode.AUTH_REQUIRED.getDefaultMessage()));
    }

    // -------------------------
    // 9999 — GENERICO
    // -------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHandler<?>> handleGeneric(Exception ex) {
        log.error("Errore interno del server", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseHandler.error(ErrorCode.GENERIC_ERROR.getCode(), ErrorCode.GENERIC_ERROR.getDefaultMessage()));
    }
}