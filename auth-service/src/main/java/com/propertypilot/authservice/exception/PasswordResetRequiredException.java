package com.propertypilot.authservice.exception;

public class PasswordResetRequiredException extends RuntimeException {
    public PasswordResetRequiredException() {
        super("Password reset obbligatorio");
    }
}
