package com.propertypilot.coreservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppartamentoDettagliDTO {

    private Integer id;
    private String nomeAppartamento;
    private String via;
    private String civico;
    private String citta;
    private String stato;
    private String cup;
    private String cin;
    private String cir;

    // dettagli appartamento
    private LocalDate dataInizioAttivita;
    private BigDecimal costoPulizie;
    private BigDecimal costoUtenzeGiornaliere;
    private BigDecimal costoTassaSoggiornoPerOspite;
    private boolean tassaSoggiornoAutomatica;
    private boolean cedolareSecca;
    private BigDecimal percentualeCommissionePm;
    private BigDecimal percentualeCommissioneHost;
    private BigDecimal percentualeCommissioneTotale;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
