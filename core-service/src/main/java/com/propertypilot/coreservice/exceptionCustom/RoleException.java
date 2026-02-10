package com.propertypilot.coreservice.exceptionCustom;

public class RoleException extends BaseServiceException {

    public RoleException(String message) {
        super(ErrorCode.ROLE_EXCEPTION, message);
    }

    public RoleException() {
        super(ErrorCode.ROLE_EXCEPTION);
    }
}