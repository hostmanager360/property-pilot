package com.propertypilot.coreservice.model;

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
@Entity
@Table(name = "appartamenti")
public class Appartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_appartamento")
    String nomeAppartamento;

    @Column(name = "via")
    String via;

    @Column(name = "civico")
    String civico;

    @Column(name = "citta")
    String citta;

    @Column(name = "stato")
    String stato;

    @Column(name = "cup")
    String cup;

    @Column(name = "cin")
    String cin;

    @Column(name = "cir")
    String cir;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updateAt;
}
