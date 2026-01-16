package com.propertypilot.coreservice.util;

import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MappingEntity {

    public static PrevisioneGuadagno toEntity(PrevisioneGuadagnoDto dto) {
        if (dto == null) {
            return null; // sicurezza null
        }

        PrevisioneGuadagno entity = new PrevisioneGuadagno();

        // Dati base
        entity.setNomeAppartamento(dto.getNomeAppartamento());
        entity.setIndirizzo(dto.getIndirizzo());
        entity.setNumeroLocali(dto.getNumeroLocali());
        entity.setNumeroBagni(dto.getNumeroBagni());
        entity.setMutuoAffitto(safeBigDecimal(dto.getMutuoAffitto()));
        entity.setTipoGestione(dto.getTipoGestione());
        entity.setAppartamentoDiretto(dto.isAppartamentoDiretto());
        entity.setCostoUtenzeMensili(safeBigDecimal(dto.getCostoUtenzeMensili()));

        // Qui puoi calcolare le utenze giornaliere se vuoi
        if (dto.getNumeroNottiMensili() > 0 && dto.getCostoUtenzeMensili() != null) {
            entity.setCostoUtenzeGiornaliere(
                    dto.getCostoUtenzeMensili().divide(BigDecimal.valueOf(dto.getNumeroNottiMensili()), BigDecimal.ROUND_HALF_UP)
            );
        } else {
            entity.setCostoUtenzeGiornaliere(BigDecimal.ZERO);
        }

        entity.setCostoPulizia(safeBigDecimal(dto.getCostoPulizia()));
        entity.setCostoTasse(safeBigDecimal(BigDecimal.valueOf(dto.getCostoTasse()))); // converti se serve percentuale
        entity.setCostoPiattaforma(safeBigDecimal(BigDecimal.valueOf(dto.getCostoPiattaforma())));
        entity.setCommissioneGestioneTotale(safeBigDecimal(BigDecimal.valueOf(dto.getCommissioneGestioneTotale())));
        entity.setCommissioneHost(safeBigDecimal(dto.getCommissioneHost()));
        entity.setCommissioneCoHost(safeBigDecimal(dto.getCommissioneCoHost()));

        entity.setNumeroPrenotazioni(dto.getNumeroPrenotazioni());
        entity.setNottiMensili(dto.getNumeroNottiMensili());
        entity.setPrezzoMedioPerNotte(safeBigDecimal(dto.getPrezzoMedioPerNotte()));

        // Totali calcolati
        entity.setTotaleLordoPernottamenti(safeBigDecimal(dto.getTotaleLordoPernottamenti()));
        entity.setTotaleCommissioneGestione(safeBigDecimal(dto.getTotaleLordoGestione())); // attenzione mapping corretto
        entity.setTotaleCostoPulizia(safeBigDecimal(dto.getTotaleCostoPulizie()));
        entity.setTotaleNettoProprietario(safeBigDecimal(dto.getTotaleNettoProprietaria()));

        return entity;
    }

    // Helper per evitare null pointer
    private static BigDecimal safeBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
