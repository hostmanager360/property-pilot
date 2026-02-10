package com.propertypilot.coreservice.exceptionCustom;

public class TenantNotFoundException extends BaseServiceException {

    public TenantNotFoundException(String message) {
        super(ErrorCode.TENANT_NOT_FOUND, message);
    }

    public TenantNotFoundException() {
        super(ErrorCode.TENANT_NOT_FOUND);
    }
}