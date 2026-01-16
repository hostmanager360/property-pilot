package com.propertypilot.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrevisioneGuadagnoDto {

    // Dati principali
    private String nomeAppartamento;
    private String indirizzo;
    private int numeroLocali;
    private int numeroBagni;
    private BigDecimal mutuoAffitto;
    private String tipoGestione;
    private BigDecimal costoUtenzeMensili;
    private BigDecimal costoPulizia;
    private int numeroPrenotazioni;
    private int numeroNottiMensili;
    private BigDecimal prezzoMedioPerNotte;

    // Valori percentuali
    private int costoTasse;            // es: 21%
    private int costoPiattaforma;      // es: 3%
    private boolean appartamentoDiretto;
    private int commissioneGestioneTotale; // es: 20%
    private BigDecimal commissioneCoHost;  // es: 5%
    private BigDecimal commissioneHost;    // es: 15%

    // Valori calcolati nel service
    private BigDecimal totaleCostoTasse;
    private BigDecimal totaleCostoPiattaforma;
    private BigDecimal totaleLordoPernottamenti;
    private BigDecimal totaleLordoGestione;
    private BigDecimal totaleCostoPulizie;
    private BigDecimal totaleNettoProprietaria;
    private BigDecimal totaleCommissioneHost;
    private BigDecimal totaleCommissioneCoHost;
}
