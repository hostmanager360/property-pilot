package com.propertypilot.coreservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppartamentoDTO {

    @NotBlank
    private String nomeAppartamento;

    @NotBlank
    private String via;

    @NotBlank
    private String civico;

    @NotBlank
    private String citta;

    @NotBlank
    private String stato;

    private String cup;
    private String cin;
    private String cir;
}
