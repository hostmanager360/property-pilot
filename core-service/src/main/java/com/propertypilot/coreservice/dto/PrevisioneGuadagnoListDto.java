package com.propertypilot.coreservice.dto;

import com.propertypilot.coreservice.model.PrevisioneGuadagno;

import java.math.BigDecimal;

public record PrevisioneGuadagnoListDto(
        Integer id,
        String nomeAppartamento,
        String indirizzo,
        int numeroBagni,
        int numeroLocali,
        BigDecimal totaleNettoProprietario,
        BigDecimal totaleLordoPernottamenti
) {
    public static PrevisioneGuadagnoListDto fromEntity(PrevisioneGuadagno p) {
        return new PrevisioneGuadagnoListDto(
                p.getId(),
                p.getNomeAppartamento(),
                p.getIndirizzo(),
                p.getNumeroBagni(),
                p.getNumeroLocali(),
                p.getTotaleNettoProprietario(),
                p.getTotaleLordoPernottamenti()
        );
    }
}
