package com.propertypilot.coreservice.exceptionCustom;

public class UserAlreadyHasTenantException extends BaseServiceException {

    public UserAlreadyHasTenantException(String message) {
        super(ErrorCode.USER_ALREADY_HAS_TENANT, message);
    }

    public UserAlreadyHasTenantException() {
        super(ErrorCode.USER_ALREADY_HAS_TENANT);
    }
}