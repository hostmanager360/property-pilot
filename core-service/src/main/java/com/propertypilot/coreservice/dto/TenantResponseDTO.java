package com.propertypilot.coreservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponseDTO {

    private String keyTenant;

    private String tipoSoggetto;

    // persona fisica
    private String nome;
    private String cognome;
    private String codiceFiscale;

    // azienda
    private String ragioneSociale;

    private String partitaIva;

    // residenza PF
    private String viaResidenza;
    private String civicoResidenza;
    private String cittaResidenza;
    private String capResidenza;

    // sede fisica
    private String viaSedeFisica;
    private String cittaSedeFisica;
    private String capSedeFisica;

    private LocalDate dataIscrizione;
    private LocalDate dataScadenza;

    private Integer tipoLicenzaId;
    private Integer statusId;
}