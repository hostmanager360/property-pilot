package com.propertypilot.coreservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tenants")
public class Tenant {

    @Id
    @Column(name = "key_tenant", length = 50)
    private String keyTenant;

    // Nuovo campo: PERSONA_FISICA | AZIENDA
    @Column(name = "tipo_soggetto", nullable = false, length = 20)
    private String tipoSoggetto;

    // Persona fisica
    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "codice_fiscale")
    private String codiceFiscale;

    // Azienda
    @Column(name = "ragione_sociale")
    private String ragioneSociale;

    // Comune a entrambi
    @Column(name = "partita_iva", nullable = false)
    private String partitaIva;

    // Residenza (solo persona fisica)
    @Column(name = "via_residenza")
    private String viaResidenza;

    @Column(name = "civico_residenza")
    private String civicoResidenza;

    @Column(name = "citta_residenza")
    private String cittaResidenza;

    @Column(name = "cap_residenza")
    private String capResidenza;

    // Sede fisica (entrambi)
    @Column(name = "via_sede_fisica", nullable = false)
    private String viaSedeFisica;

    @Column(name = "citta_sede_fisica", nullable = false)
    private String cittaSedeFisica;

    @Column(name = "cap_sede_fisica", nullable = false)
    private String capSedeFisica;

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