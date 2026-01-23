package com.propertypilot.coreservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TenantResponseDTO {
    private String keyTenant;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String partitaIva;
    private String viaResidenza;
    private String civicoResidenza;
    private String cittaResidenza;
    private String capResidenza;
    private LocalDate dataIscrizione;
    private LocalDate dataScadenza;
    private String tipoLicenza;
    private String status;
}

