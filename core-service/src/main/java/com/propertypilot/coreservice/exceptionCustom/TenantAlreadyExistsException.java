package com.propertypilot.coreservice.exceptionCustom;

public class TenantAlreadyExistsException extends BaseServiceException {

    public TenantAlreadyExistsException(String message) {
        super(ErrorCode.TENANT_ALREADY_EXISTS, message);
    }

    public TenantAlreadyExistsException() {
        super(ErrorCode.TENANT_ALREADY_EXISTS);
    }
}