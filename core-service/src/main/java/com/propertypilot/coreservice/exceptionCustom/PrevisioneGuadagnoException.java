package com.propertypilot.coreservice.exceptionCustom;

public class PrevisioneGuadagnoException extends RuntimeException {

    private final int code;

    public PrevisioneGuadagnoException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}