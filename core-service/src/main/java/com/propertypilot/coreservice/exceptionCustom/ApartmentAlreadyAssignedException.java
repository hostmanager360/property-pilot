package com.propertypilot.coreservice.exceptionCustom;

public class ApartmentAlreadyAssignedException extends RuntimeException {
    public ApartmentAlreadyAssignedException(String message) {
        super(message);
    }
}
