package com.propertypilot.authservice.exception;

import com.propertypilot.authservice.dto.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHandler.error(1001, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseHandler.error(1002, ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetRequiredException.class)
    public ResponseEntity<?> handlePasswordResetRequired(PasswordResetRequiredException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseHandler.error(1003, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseHandler.error(1999, "Errore interno"));
    }
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<?> notActivateAccount(AccountNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseHandler.error(1002, ex.getMessage()));
    }
}