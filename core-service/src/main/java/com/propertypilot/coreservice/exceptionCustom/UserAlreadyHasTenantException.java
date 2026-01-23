package com.propertypilot.coreservice.exceptionCustom;

public class UserAlreadyHasTenantException extends RuntimeException {
    public UserAlreadyHasTenantException(String message) {
        super(message);
    }
}
