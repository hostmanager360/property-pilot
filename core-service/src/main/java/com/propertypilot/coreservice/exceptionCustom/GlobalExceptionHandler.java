package com.propertypilot.coreservice.exceptionCustom;

import com.propertypilot.coreservice.dto.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------------------------------------------
    // 1000 — VALIDAZIONE
    // ---------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseHandler<?>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(1001, "Errore di validazione"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(1002, "JSON non valido"));
    }

    @ExceptionHandler(InvalidTenantDataException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidTenantData(InvalidTenantDataException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(1003, ex.getMessage()));
    }

    // ---------------------------------------------------------
    // 2000 — TENANT
    // ---------------------------------------------------------

    @ExceptionHandler(TenantAlreadyExistsException.class)
    public ResponseEntity<ResponseHandler<?>> handleTenantExists(TenantAlreadyExistsException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(2001, ex.getMessage()));
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTenantNotFound(TenantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(2002, ex.getMessage()));
    }

    @ExceptionHandler(StatusTenantNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleStatusTenantNotFound(StatusTenantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(2003, ex.getMessage()));
    }

    @ExceptionHandler(TipoLicenzaNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTipoLicenzaNotFound(TipoLicenzaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(2004, ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyHasTenantException.class)
    public ResponseEntity<ResponseHandler<?>> handleUserAlreadyHasTenant(UserAlreadyHasTenantException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseHandler.error(2005, ex.getMessage()));
    }

    // ---------------------------------------------------------
    // 3000 — SICUREZZA / PERMESSI
    // ---------------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseHandler<?>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(3001, "Accesso negato"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseHandler<?>> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseHandler.error(3002, "Autenticazione richiesta"));
    }

    // ---------------------------------------------------------
    // 9999 — GENERICO
    // ---------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHandler<?>> handleGeneric(Exception ex) {
        return ResponseEntity.status(500)
                .body(ResponseHandler.error(9999, "Errore interno del server"));
    }
}



