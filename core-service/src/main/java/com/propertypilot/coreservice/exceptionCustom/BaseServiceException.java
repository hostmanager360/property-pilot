package com.propertypilot.coreservice.exceptionCustom;

import lombok.Getter;

@Getter
public abstract class BaseServiceException extends RuntimeException {

    private final int code;

    protected BaseServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    protected BaseServiceException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.code = errorCode.getCode();
    }

    protected BaseServiceException(int code, String message) {
        super(message);
        this.code = code;
    }
}