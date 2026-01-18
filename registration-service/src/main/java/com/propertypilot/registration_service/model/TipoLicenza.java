package com.propertypilot.registration_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_licenza")
public class TipoLicenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codice_licenza", nullable = false, unique = true)
    private String codiceLicenza;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "costo")
    private BigDecimal costo;
}