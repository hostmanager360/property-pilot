package com.propertypilot.authservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("Utente non trovato: " + email);
    }
}

