package com.propertypilot.registration_service.exception;

public class StepNotFoundException extends RuntimeException {
    public StepNotFoundException(String message) {
        super(message);
    }
}
