package com.propertypilot.exceptionCustom;

public class PrenotazioneException extends RuntimeException {
    private int code;

    public PrenotazioneException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}