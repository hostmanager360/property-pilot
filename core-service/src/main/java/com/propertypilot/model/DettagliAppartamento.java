package com.propertypilot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "tenant_key", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_gestione_id")
    private TipoGestione tipoGestione;

    @Column(name = "data_inizio_attivita")
    Date dataInizioAttivita;

    @Column(name = "costo_pulizie")
    double costoPulizie;

    @Column(name = "costo_utenze_giornaliere")
    double costoUtenzeGiornaliere;

    @Column(name = "costo_tassa_soggiorno_per_ospite")
    double costoTassaSoggiornoPerOspite;

    @Column(name = "tassa_soggiorno_automatica")
    boolean tassaSoggiornoAutomatica;

    @Column(name = "cedolare_secca")
    boolean cedolareSecca;

    @Column(name = "percentuale_commissione_pm")
    double percentualeCommissionePm;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "update_at")
    LocalDateTime updateAt;
}
