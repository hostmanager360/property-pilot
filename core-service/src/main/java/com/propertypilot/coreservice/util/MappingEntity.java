package com.propertypilot.coreservice.util;

import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
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
        entity.setTotaleLordoGestione(safeBigDecimal(dto.getTotaleLordoGestione())); // attenzione mapping corretto
        entity.setTotaleCostoPulizia(safeBigDecimal(dto.getTotaleCostoPulizie()));
        entity.setTotaleNettoProprietario(safeBigDecimal(dto.getTotaleNettoProprietario()));
        entity.setTotaleLordoGestione(safeBigDecimal(dto.getTotaleLordoGestione()));
        entity.setTotaleCommissioneCoHost(safeBigDecimal(dto.getTotaleCommissioneCoHost()));
        entity.setTotaleCommissioneHost(safeBigDecimal(dto.getTotaleCommissioneHost()));
        entity.setTotaleCostoTassa(safeBigDecimal(dto.getTotaleCostoTassa()));
        entity.setTotaleCostoPiattaforma(safeBigDecimal(dto.getTotaleCostoPiattaforma()));

        return entity;
    }
    public static PrevisioneGuadagnoDto toDto(PrevisioneGuadagno e) {
        PrevisioneGuadagnoDto dto = new PrevisioneGuadagnoDto();
        dto.setNomeAppartamento(e.getNomeAppartamento());
        dto.setIndirizzo(e.getIndirizzo());
        dto.setNumeroLocali(e.getNumeroLocali());
        dto.setNumeroBagni(e.getNumeroBagni());
        dto.setMutuoAffitto(e.getMutuoAffitto());
        dto.setTipoGestione(e.getTipoGestione());
        dto.setCostoUtenzeMensili(e.getCostoUtenzeMensili());
        dto.setCostoPulizia(e.getCostoPulizia());
        dto.setNumeroPrenotazioni(e.getNumeroPrenotazioni());
        dto.setNumeroNottiMensili(e.getNottiMensili());
        dto.setPrezzoMedioPerNotte(e.getPrezzoMedioPerNotte());

        dto.setCostoTasse(e.getCostoTasse().intValue());
        dto.setCostoPiattaforma(e.getCostoPiattaforma().intValue());
        dto.setAppartamentoDiretto(e.isAppartamentoDiretto());
        dto.setCommissioneGestioneTotale(e.getCommissioneGestioneTotale().intValue());
        dto.setCommissioneCoHost(e.getCommissioneCoHost());
        dto.setCommissioneHost(e.getCommissioneHost());

        dto.setTotaleCostoTassa(e.getTotaleCostoTassa());
        dto.setTotaleCostoPiattaforma(e.getTotaleCostoPiattaforma());
        dto.setTotaleLordoPernottamenti(e.getTotaleLordoPernottamenti());
        dto.setTotaleLordoGestione(e.getTotaleLordoGestione());
        dto.setTotaleCostoPulizie(e.getTotaleCostoPulizia());
        dto.setTotaleNettoProprietario(e.getTotaleNettoProprietario());
        dto.setTotaleCommissioneHost(e.getTotaleCommissioneHost());
        dto.setTotaleCommissioneCoHost(e.getTotaleCommissioneCoHost());

        return dto;
    }
    // Helper per evitare null pointer
    private static BigDecimal safeBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
