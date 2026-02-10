package com.propertypilot.coreservice.exceptionCustom;

public class StatusTenantNotFoundException extends BaseServiceException {

    public StatusTenantNotFoundException(String message) {
        super(ErrorCode.STATUS_TENANT_NOT_FOUND, message);
    }

    public StatusTenantNotFoundException() {
        super(ErrorCode.STATUS_TENANT_NOT_FOUND);
    }
}