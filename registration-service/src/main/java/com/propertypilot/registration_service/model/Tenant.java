package com.propertypilot.registration_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @Column(name = "key_tenant", length = 50)
    private String keyTenant;

    @Column(name = "nome",nullable = false)
    private String nome;

    @Column(name ="cognome" ,nullable = false)
    private String cognome;

    @Column(name = "codice_fiscale")
    private String codiceFiscale;

    @Column(name = "partita_iva")
    private String partitaIva;

    @Column(name = "via_residenza")
    private String viaResidenza;

    @Column(name = "civico_residenza")
    private String civicoResidenza;

    @Column(name = "citta_residenza")
    private String cittaResidenza;

    @Column(name = "cap_residenza")
    private String capResidenza;

    @Column(name = "data_iscrizione", nullable = false)
    private LocalDate dataIscrizione;

    @Column(name = "data_scadenza")
    private LocalDate dataScadenza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_licenza_id")
    private TipoLicenza tipoLicenza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private StatusTenant status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}