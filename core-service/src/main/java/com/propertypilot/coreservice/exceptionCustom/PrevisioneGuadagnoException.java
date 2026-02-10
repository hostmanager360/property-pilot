package com.propertypilot.coreservice.exceptionCustom;

import lombok.Getter;

@Getter
public class PrevisioneGuadagnoException extends BaseServiceException {

    public PrevisioneGuadagnoException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PrevisioneGuadagnoException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PrevisioneGuadagnoException(int code, String message) {
        super(code, message);
    }
}