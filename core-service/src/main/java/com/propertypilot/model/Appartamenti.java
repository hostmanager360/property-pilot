package com.propertypilot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name= "appartamenti")
public class Appartamenti {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "tenant_key")
    String tenantKey;

    @Column(name = "nome_appartamento")
    String nomeAppartamento;

    @Column(name = "via")
    String via;

    @Column(name = "civico")
    int civico;

    @Column(name = "citta")
    String citta;

    @Column(name = "stato")
    String stato;

    @Column(name = "cup")
    int cup;

    @Column(name = "cin")
    String cin;

    @Column(name = "cir")
    String cir;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "update_at")
    LocalDateTime updateAt;
}
