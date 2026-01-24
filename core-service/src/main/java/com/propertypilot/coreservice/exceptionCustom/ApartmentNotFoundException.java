package com.propertypilot.coreservice.exceptionCustom;

public class ApartmentNotFoundException extends RuntimeException {
    public ApartmentNotFoundException(String message) {
        super(message);
    }
}
