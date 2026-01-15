package com.propertypilot.coreservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prenotazioni")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chiave_prenotazione")
    private String chiavePrenotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartamento_id")
    private Appartamento appartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_key")
    private Tenant tenant;

    @Column(name = "data_inizio", nullable = false)
    private LocalDate dataInizio;

    @Column(name = "data_fine", nullable = false)
    private LocalDate dataFine;

    @Column(name = "titolare_prenotazione")
    private String titolarePrenotazione;

    @Column(name = "costo")
    private BigDecimal costo;

    @Column(name = "numero_ospiti")
    private Integer numeroOspiti;

    @Column(name = "data_check_in_online")
    private LocalDateTime dataCheckInOnline;

    @Column(name = "costo_city_tax")
    private BigDecimal costoCityTax;

    @Column(name = "status_alloggiati_web")
    private String statusAlloggiatiWeb;

    @Column(name = "costo_cedolare_secca")
    private BigDecimal costoCedolareSecca;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
