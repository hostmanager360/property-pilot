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
@Entity(name= "ospiti")
public class Ospite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Prenotazione prenotazione;

    @Column(name = "tenant_key")
    String tenantKey;

    @Column(name = "appartamento_id")
    int appartamentoId;

    @Column(name = "nome")
    String nome;

    @Column(name = "cognome")
    String cognome;

    @Column(name = "data_nascita")
    Date dataNascita;

    @Column(name = "cittadinanza")
    String cittadinanza;

    @Column(name = "citta_residenza")
    String cittaResidenza;

    @Column(name = "id_documento")
    String idDocumento;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
