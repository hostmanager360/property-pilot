package com.propertypilot.authservice.exception;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException() {
        super("Utente non confermato");
    }
}
