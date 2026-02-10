package com.propertypilot.registration_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // -------------------------
    // 1000 — VALIDAZIONE
    // -------------------------
    EMAIL_ALREADY_EXISTS(1001, "Email già registrata"),
    INVALID_EMAIL(1002, "Email non valida"),
    PASSWORD_MISMATCH(1003, "Le password non coincidono"),
    INVALID_JSON(1004, "JSON non valido"),
    INVALID_TENANT(1005, "Tenant non valido"),

    // -------------------------
    // 2000 — EMAIL
    // -------------------------
    EMAIL_SEND_ERROR(2001, "Errore durante l'invio dell'email"),

    // -------------------------
    // 3000 — TOKEN
    // -------------------------
    TOKEN_NOT_FOUND(3001, "Token non valido"),
    TOKEN_EXPIRED(3002, "Token scaduto"),

    // -------------------------
    // 4000 — PERMESSI / RUOLI
    // -------------------------
    FORBIDDEN(4001, "Accesso negato"),
    ROLE_NOT_FOUND(4002, "Ruolo non trovato"),
    STEP_NOT_FOUND(4003, "Step non trovato"),
    ACCESS_DENIED(4004, "Accesso negato"),
    AUTH_REQUIRED(4005, "Autenticazione richiesta"),

    // -------------------------
    // 9999 — GENERICO
    // -------------------------
    GENERIC_ERROR(9999, "Errore interno del server");

    private final int code;
    private final String defaultMessage;
}