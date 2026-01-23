package com.propertypilot.coreservice.exceptionCustom;

public class InvalidTenantDataException extends RuntimeException {
    public InvalidTenantDataException(String msg) { super(msg); }
}
