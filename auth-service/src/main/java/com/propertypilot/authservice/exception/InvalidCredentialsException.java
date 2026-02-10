package com.propertypilot.authservice.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenziali non valide");
    }
}



