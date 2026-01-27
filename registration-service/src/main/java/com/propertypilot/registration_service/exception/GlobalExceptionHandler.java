package com.propertypilot.registration_service.exception;

import com.propertypilot.registration_service.model.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseHandler<?>> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.error(1001, ex.getMessage())
        );
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ResponseHandler<?>> handleInvalidEmail(InvalidEmailException ex) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.error(1002, ex.getMessage())
        );
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ResponseHandler<?>> handleEmailSend(EmailSendException ex) {
        return ResponseEntity.status(500).body(
                ResponseHandler.error(2001, ex.getMessage())
        );
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ResponseHandler<?>> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.error(3001, ex.getMessage())
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ResponseHandler<?>> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.error(3002, ex.getMessage())
        );
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseHandler<Object>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(4001, ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHandler<?>> handleGeneric(Exception ex) {
        return ResponseEntity.status(500).body(
                ResponseHandler.error(9999, "Errore interno del server")
        );
    }
}