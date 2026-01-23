package com.propertypilot.coreservice.exceptionCustom;

public class StatusTenantNotFoundException extends RuntimeException {
    public StatusTenantNotFoundException(String msg) { super(msg); }
}
