package com.propertypilot.coreservice.exceptionCustom;

public class InvalidTenantDataException extends BaseServiceException {

    public InvalidTenantDataException(String message) {
        super(ErrorCode.INVALID_TENANT_DATA, message);
    }

    public InvalidTenantDataException() {
        super(ErrorCode.INVALID_TENANT_DATA);
    }
}
