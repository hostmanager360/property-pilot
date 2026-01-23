package com.propertypilot.coreservice.exceptionCustom;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}
