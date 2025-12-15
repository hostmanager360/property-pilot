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
@Entity(name= "dettagli_appartamenti")
public class DettagliAppartamenti {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "appartamento_id")
    int appartamentoId;

    @Column(name = "tenant_key")
    String tenantKey;

    @Column(name = "tipo_gestione_id")
    int tipoGestioneId;

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
