package com.propertypilot.coreservice.exceptionCustom;

import com.propertypilot.coreservice.dto.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.propertypilot.coreservice.exceptionCustom.ErrorCode.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ---------------------------------------------------------
    // 1000 — VALIDAZIONE
    // ---------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseHandler<?>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Errore di validazione: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        ErrorCode.VALIDATION_ERROR.getDefaultMessage()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidJson(HttpMessageNotReadableException ex) {
        log.warn("JSON non valido", ex);
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(
                        INVALID_JSON.getCode(),
                        INVALID_JSON.getDefaultMessage()
                ));
    }

    @ExceptionHandler(InvalidTenantDataException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidTenantData(InvalidTenantDataException ex) {
        log.warn("Dati tenant non validi: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(
                        ErrorCode.INVALID_TENANT_DATA.getCode(),
                        ex.getMessage()
                ));
    }

    // ---------------------------------------------------------
    // 2000 — TENANT
    // ---------------------------------------------------------

    @ExceptionHandler(TenantAlreadyExistsException.class)
    public ResponseEntity<ResponseHandler<?>> handleTenantExists(TenantAlreadyExistsException ex) {
        log.warn("Tenant già esistente: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(
                        ErrorCode.TENANT_ALREADY_EXISTS.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTenantNotFound(TenantNotFoundException ex) {
        log.warn("Tenant non trovato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(
                        ErrorCode.TENANT_NOT_FOUND.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(StatusTenantNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleStatusTenantNotFound(StatusTenantNotFoundException ex) {
        log.warn("Stato tenant non trovato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(
                        ErrorCode.STATUS_TENANT_NOT_FOUND.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(TipoLicenzaNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTipoLicenzaNotFound(TipoLicenzaNotFoundException ex) {
        log.warn("Tipo licenza non trovato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(
                        ErrorCode.TIPO_LICENZA_NOT_FOUND.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(UserAlreadyHasTenantException.class)
    public ResponseEntity<ResponseHandler<?>> handleUserAlreadyHasTenant(UserAlreadyHasTenantException ex) {
        log.warn("Utente ha già un tenant: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(
                        ErrorCode.USER_ALREADY_HAS_TENANT.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RoleException.class)
    public ResponseEntity<ResponseHandler<?>> handleRoleException(RoleException ex) {
        log.warn("Errore ruolo: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(
                        ErrorCode.ROLE_EXCEPTION.getCode(),
                        ex.getMessage()
                ));
    }

    // ---------------------------------------------------------
    // 3000 — SICUREZZA / PERMESSI
    // ---------------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseHandler<?>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Accesso negato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(
                        ErrorCode.ACCESS_DENIED.getCode(),
                        ErrorCode.ACCESS_DENIED.getDefaultMessage()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseHandler<?>> handleAuth(AuthenticationException ex) {
        log.warn("Autenticazione richiesta: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseHandler.error(
                        AUTH_REQUIRED.getCode(),
                        AUTH_REQUIRED.getDefaultMessage()
                ));
    }

    // ---------------------------------------------------------
    // 9999 — GENERICO
    // ---------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHandler<?>> handleGeneric(Exception ex) {
        log.error("Errore interno del server", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseHandler.error(
                        ErrorCode.GENERIC_ERROR.getCode(),
                        ErrorCode.GENERIC_ERROR.getDefaultMessage()
                ));
    }

    @ExceptionHandler(PrevisioneGuadagnoException.class)
    public ResponseEntity<ResponseHandler<?>> handlePrevisione(PrevisioneGuadagnoException ex) {
        log.warn("PrevisioneGuadagnoException code={} msg={}", ex.getErrorCode().getCode(), ex.getMessage());

        HttpStatus status = switch (ex.getErrorCode()) {
            case PREVISIONE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR, INVALID_JSON -> HttpStatus.BAD_REQUEST;
            case AUTH_REQUIRED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status).body(ResponseHandler.error(
                ex.getErrorCode().getCode(),
                ex.getMessage()
        ));
    }
}