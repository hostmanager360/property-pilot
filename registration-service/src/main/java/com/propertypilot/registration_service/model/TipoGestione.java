package com.propertypilot.registration_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_gestione")
public class TipoGestione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cod_gestione", nullable = false, unique = true)
    private String codGestione;

    @Column(name = "descrizione")
    private String descrizione;
}