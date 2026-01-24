package com.propertypilot.coreservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dettagli_appartamenti")
public class DettagliAppartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartamento_id", nullable = false)
    private Appartamento appartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_gestione_id")
    private TipoGestione tipoGestione;

    @Column(name = "data_inizio_attivita")
    LocalDate dataInizioAttivita;

    @Column(name = "costo_pulizie")
    BigDecimal  costoPulizie;

    @Column(name = "costo_utenze_giornaliere")
    BigDecimal costoUtenzeGiornaliere;

    @Column(name = "costo_tassa_soggiorno_per_ospite")
    BigDecimal  costoTassaSoggiornoPerOspite;

    @Column(name = "tassa_soggiorno_automatica")
    boolean  tassaSoggiornoAutomatica;

    @Column(name = "cedolare_secca")
    boolean  cedolareSecca;

    @Column(name = "percentuale_commissione_pm")
    BigDecimal  percentualeCommissionePm;

    @Column(name = "percentuale_commissione_host")
    private BigDecimal percentualeCommissioneHost;

    @Column(name = "percentuale_commissione_totale")
    private BigDecimal percentualeCommissioneTotale;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updateAt;
}
