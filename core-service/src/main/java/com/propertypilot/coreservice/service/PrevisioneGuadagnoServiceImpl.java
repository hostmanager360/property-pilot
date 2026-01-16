package com.propertypilot.coreservice.service;


import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PrevisioneGuadagnoServiceImpl implements PrevisioneGadagnoService{

    @Override
    public PrevisioneGuadagnoDto calcoloCostiPrevisioneGadagno(PrevisioneGuadagnoDto dto) {
        PrevisioneGuadagnoDto result = new PrevisioneGuadagnoDto();

        BigDecimal totaleLordoPernottamenti;
        BigDecimal totaleCostoTassa;
        BigDecimal totaleCostoPiattaforma;
        BigDecimal totaleLordoGestione;
        BigDecimal totaleCostoPulizie;
        BigDecimal totaleNettoProprietaria;
        BigDecimal totaleCommisioneHost;
        BigDecimal totaleCommisioneCoHost;


        totaleLordoPernottamenti = calcoloTotaleLordoPernottamenti(dto.getPrezzoMedioPerNotte() , dto.getNumeroNottiMensili());
        result.setTotaleLordoPernottamenti(totaleLordoPernottamenti);

        totaleCostoTassa = calcoloCostotasse(dto.getCostoTasse(), dto.getTotaleLordoPernottamenti());
        result.setTotaleCostoTasse(totaleCostoTassa);

        totaleCostoPiattaforma = calcoloCostoPiattaforma(dto.getCostoPiattaforma(), dto.getTotaleLordoPernottamenti());
        result.setTotaleCostoPiattaforma(totaleCostoPiattaforma);

        totaleLordoGestione = calcoloTotaleLordoGestione(dto.getCommissioneGestioneTotale(), dto.getTotaleLordoPernottamenti());
        result.setTotaleLordoGestione(totaleLordoGestione);

        totaleCostoPulizie = calcoloTotaleCostoPulizie(dto.getCostoPulizia(), dto.getNumeroPrenotazioni());
        result.setTotaleCostoPulizie(totaleCostoPulizie);

        totaleNettoProprietaria =calcoloTotaleNettoProprietaria(dto.getTotaleCostoTasse(), dto.getTotaleCostoPiattaforma(), dto.getTotaleLordoPernottamenti(), dto.getTotaleLordoGestione(), dto.getTotaleCostoPulizie(), dto.getCostoUtenzeMensili(), dto.getMutuoAffitto());
        result.setTotaleNettoProprietaria(totaleNettoProprietaria);

        totaleCommisioneHost = calcoloTotaleCommisioneHost(dto.getCommissioneHost(), dto.getTotaleLordoPernottamenti());
        result.setTotaleCommissioneHost(totaleCommisioneHost);

        totaleCommisioneCoHost = calcoloTotaleCommisioneCoHost(dto.getCommissioneCoHost(), dto.getTotaleLordoPernottamenti());
        result.setTotaleCommissioneCoHost(totaleCommisioneCoHost);





        return result;
    }


    private static BigDecimal calcoloTotaleLordoPernottamenti(BigDecimal prezzoMedioPerNotte, int numeroNottiMensili) {
        BigDecimal totaleLordoPernottamenti;
        totaleLordoPernottamenti = prezzoMedioPerNotte.multiply(BigDecimal.valueOf(numeroNottiMensili));

        return totaleLordoPernottamenti;
    }

    private BigDecimal calcoloCostotasse (int percentualeTasse, BigDecimal totaleLordoPernottamenti) {
        BigDecimal totaleCostoTassa;
        totaleCostoTassa = totaleLordoPernottamenti.multiply( BigDecimal.valueOf(percentualeTasse)
                                                   .divide(BigDecimal.valueOf(100)));

        return totaleCostoTassa;
    }

    private BigDecimal calcoloCostoPiattaforma(int costoPiattaforma, BigDecimal totaleLordoPernottamenti) {
        BigDecimal totaleCostoPiattaforma;
        totaleCostoPiattaforma = totaleLordoPernottamenti.multiply( BigDecimal.valueOf(costoPiattaforma)
                                                         .divide(BigDecimal.valueOf(100)));
        return totaleCostoPiattaforma;
    }

    private BigDecimal calcoloTotaleLordoGestione(int commissioneGestionetotale, BigDecimal totaleLordoPernottamenti) {
        BigDecimal totaleCostoGestione;
        totaleCostoGestione= totaleLordoPernottamenti.multiply( BigDecimal.valueOf(commissioneGestionetotale)
                                                     .divide(BigDecimal.valueOf(100)));
        return totaleCostoGestione;
    }

    private BigDecimal calcoloTotaleCostoPulizie(BigDecimal costoPulizia, int numeroPrenotazioni) {
        BigDecimal totaleCostoPulizie;
        totaleCostoPulizie = costoPulizia.multiply(BigDecimal.valueOf(numeroPrenotazioni));
        return totaleCostoPulizie;
    }

    private BigDecimal calcoloTotaleNettoProprietaria(BigDecimal totaleCostoTassa, BigDecimal totaleCostoPiattaforma, BigDecimal totaleLordoPernottamenti, BigDecimal totaleLordoGestione, BigDecimal totaleCostoPulizie, BigDecimal costoUtenzeMensili, BigDecimal mutuoAffitto) {
        BigDecimal totaleNettoProprietaria;
        totaleNettoProprietaria = totaleLordoPernottamenti.subtract(totaleCostoPiattaforma)
                                                          .subtract(totaleCostoTassa)
                                                          .subtract(totaleLordoGestione)
                                                          .subtract(totaleCostoPulizie)
                                                          .subtract(costoUtenzeMensili)
                                                          .subtract(mutuoAffitto);
        return totaleNettoProprietaria;
    }

    private BigDecimal calcoloTotaleCommisioneHost(BigDecimal commissioneHost, BigDecimal totaleLordoPernottamenti) {
        BigDecimal totaleCommisioneHost;
        totaleCommisioneHost = totaleLordoPernottamenti.multiply((commissioneHost)
                                                       .divide(BigDecimal.valueOf(100)));
        return totaleCommisioneHost;
    }

    private BigDecimal calcoloTotaleCommisioneCoHost(BigDecimal commissioneCoHost, BigDecimal totaleLordoPernottamenti) {
        BigDecimal totaleCommisioneCoHost;
        totaleCommisioneCoHost = totaleLordoPernottamenti.multiply((commissioneCoHost)
                                                         .divide(BigDecimal.valueOf(100)));
        return totaleCommisioneCoHost;
    }



}
