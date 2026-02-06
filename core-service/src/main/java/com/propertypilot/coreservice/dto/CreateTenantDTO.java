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

    @NotBlank
    private String tipoSoggetto; // PERSONA_FISICA | AZIENDA

    // Persona fisica
    private String nome;
    private String cognome;
    private String codiceFiscale;

    // Dati aziendali
    private String ragioneSociale;

    @NotBlank
    private String partitaIva;

    // Residenza (solo persona fisica)
    private String viaResidenza;
    private String civicoResidenza;
    private String cittaResidenza;
    private String capResidenza;

    // Sede fisica (entrambi i casi)
    @NotBlank
    private String viaSedeFisica;

    @NotBlank
    private String cittaSedeFisica;

    @NotBlank
    private String capSedeFisica;

    @NotNull
    private LocalDate dataIscrizione;

    private LocalDate dataScadenza;

    @NotNull
    private Integer tipoLicenzaId;

    @NotNull
    private Integer statusId;
}
