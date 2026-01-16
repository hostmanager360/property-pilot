package com.propertypilot.coreservice.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "previsioni_guadagno")
public class PrevisioneGuadagno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_appartamento")
    String nomeAppartamento;

    @Column(name = "indirizzo")
    String indirizzo;

    @Column(name = "numero_bagni")
    int numeroBagni;

    @Column(name = "numero_locali")
    int numeroLocali;

    @Column(name = "mutuo_affitto")
    BigDecimal mutuoAffitto;

    @Column(name = "tipo_gestione")
    String tipoGestione;

    @Column(name = "appartamento_diretto")
    boolean appartamentoDiretto;

    @Column(name = "costo_utenze_mensili")
    BigDecimal costoUtenzeMensili;

    @Column(name = "costo_utenze_giornaliere")
    BigDecimal costoUtenzeGiornaliere;

    @Column(name = "costo_pulizia")
    BigDecimal costoPulizia;

    @Column(name = "costo_tasse")
    BigDecimal costoTasse;

    @Column(name = "costo_piattaforma")
    BigDecimal costoPiattaforma;

    @Column(name = "commissione_gestione_totale")
    BigDecimal commissioneGestioneTotale;

    @Column(name = "commissione_host")
    BigDecimal commissioneHost;

    @Column(name = "commissione_co_host")
    BigDecimal commissioneCoHost;

    @Column(name = "numero_prenotazioni")
    int numeroPrenotazioni;

    @Column(name = "notti_mensili")
    int nottiMensili;

    @Column(name = "prezzo_medio_per_notte")
    BigDecimal prezzoMedioPerNotte;

    @Column(name = "totale_lordo_pernottamenti")
    BigDecimal totaleLordoPernottamenti;

    @Column(name = "totale_commissione_gestione")
    BigDecimal totaleCommissioneGestione;

    @Column(name = "totale_costo_pulizia")
    BigDecimal totaleCostoPulizia;

    @Column(name = "totale_netto_proprietario")
    BigDecimal totaleNettoProprietario;

}
