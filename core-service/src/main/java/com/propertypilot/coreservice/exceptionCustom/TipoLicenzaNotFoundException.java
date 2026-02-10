package com.propertypilot.coreservice.exceptionCustom;

public class TipoLicenzaNotFoundException extends BaseServiceException {

    public TipoLicenzaNotFoundException(String message) {
        super(ErrorCode.TIPO_LICENZA_NOT_FOUND, message);
    }

    public TipoLicenzaNotFoundException() {
        super(ErrorCode.TIPO_LICENZA_NOT_FOUND);
    }
}