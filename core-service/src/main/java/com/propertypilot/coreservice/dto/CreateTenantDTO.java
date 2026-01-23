package com.propertypilot.coreservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTenantDTO {

    @NotNull
    private int userId;
    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotBlank
    private String codiceFiscale;

    @NotBlank
    private String partitaIva;

    @NotBlank
    private String viaResidenza;

    @NotBlank
    private String civicoResidenza;

    @NotBlank
    private String cittaResidenza;

    @NotBlank
    private String capResidenza;

    @NotNull
    private LocalDate dataIscrizione;

    private LocalDate dataScadenza;

    @NotNull
    private Integer tipoLicenzaId;

    @NotNull
    private Integer statusId;
}
