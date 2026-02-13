package com.propertypilot.coreservice.exceptionCustom;

import lombok.Getter;

@Getter
public abstract class BaseServiceException extends RuntimeException {

    private final ErrorCode errorCode;   // <-- serve per switch e log
    private final int code;

    protected BaseServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
    }

    protected BaseServiceException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
    }

    protected BaseServiceException(int code, String message) {
        super(message);
        this.errorCode = null;
        this.code = code;
    }
}