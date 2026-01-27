package com.propertypilot.coreservice.exceptionCustom;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }
}
