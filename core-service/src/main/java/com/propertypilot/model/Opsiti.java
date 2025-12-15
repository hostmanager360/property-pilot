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
@Entity(name= "opsiti")
public class Opsiti {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "prenotazione_key")
    String prenotazioneKey;

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
