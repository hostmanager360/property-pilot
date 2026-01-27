package com.propertypilot.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    private String nome;
    private String cognome;
    private String telefono;
    private LocalDate dataNascita;

}
