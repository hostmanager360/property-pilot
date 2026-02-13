package com.propertypilot.coreservice.exceptionCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    PREVISIONE_NOT_FOUND(4101, "Previsione non trovata"),
    PREVISIONE_FORBIDDEN(4102, "Accesso non consentito alla previsione"),
    // ---------------------------------------------------------
    // 1000 — VALIDAZIONE
    // ---------------------------------------------------------
    VALIDATION_ERROR(1001, "Errore di validazione"),
    INVALID_JSON(1002, "JSON non valido"),
    INVALID_TENANT_DATA(1003, "Dati tenant non validi"),
    PREVISIONE_PDF_EMPTY(4103, "PDF generato vuoto"),
    PREVISIONE_PDF_ERROR(4104, "Errore generazione PDF"),
    // ---------------------------------------------------------
    // 2000 — TENANT
    // ---------------------------------------------------------
    TENANT_ALREADY_EXISTS(2001, "Tenant già esistente"),
    TENANT_NOT_FOUND(2002, "Tenant non trovato"),
    STATUS_TENANT_NOT_FOUND(2003, "Stato tenant non trovato"),
    TIPO_LICENZA_NOT_FOUND(2004, "Tipo licenza non trovato"),
    USER_ALREADY_HAS_TENANT(2005, "L'utente ha già un tenant"),
    ROLE_EXCEPTION(2006, "Ruolo non valido"),

    // ---------------------------------------------------------
    // 3000 — SICUREZZA / PERMESSI
    // ---------------------------------------------------------
    ACCESS_DENIED(3001, "Accesso negato"),
    AUTH_REQUIRED(3002, "Autenticazione richiesta"),

    // ---------------------------------------------------------
    // 9999 — GENERICO
    // ---------------------------------------------------------
    GENERIC_ERROR(9999, "Errore interno del server");

    private final int code;
    private final String defaultMessage;
}