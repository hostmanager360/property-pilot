package com.propertypilot.coreservice.service;


import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.exceptionCustom.PrevisioneGuadagnoException;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.repository.PrevisioneGadagnoRepository;
import com.propertypilot.coreservice.util.MappingEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PrevisioneGuadagnoServiceImpl implements PrevisioneGadagnoService{

    @Autowired
    PrevisioneGadagnoRepository pgRepository;

    @Transactional
    @Override
    public PrevisioneGuadagno calcoloCostiPrevisioneGadagno(PrevisioneGuadagnoDto dto) {
        validateDto(dto);

        try{
            BigDecimal totaleLordoPernottamenti;
            BigDecimal totaleCostoTassa;
            BigDecimal totaleCostoPiattaforma;
            BigDecimal totaleLordoGestione;
            BigDecimal totaleCostoPulizie;
            BigDecimal totaleNettoProprietaria;
            BigDecimal totaleCommisioneHost;
            BigDecimal totaleCommisioneCoHost;


            totaleLordoPernottamenti = calcoloTotaleLordoPernottamenti(dto.getPrezzoMedioPerNotte() , dto.getNumeroNottiMensili());
            dto.setTotaleLordoPernottamenti(totaleLordoPernottamenti);

            totaleCostoTassa = calcoloCostotasse(dto.getCostoTasse(), totaleLordoPernottamenti);
            dto.setTotaleCostoTassa(totaleCostoTassa);

            totaleCostoPiattaforma = calcoloCostoPiattaforma(dto.getCostoPiattaforma(), totaleLordoPernottamenti);
            dto.setTotaleCostoPiattaforma(totaleCostoPiattaforma);

            totaleLordoGestione = calcoloTotaleLordoGestione(dto.getCommissioneGestioneTotale(), totaleLordoPernottamenti, totaleCostoPiattaforma);
            dto.setTotaleLordoGestione(totaleLordoGestione);

            totaleCostoPulizie = calcoloTotaleCostoPulizie(dto.getCostoPulizia(), dto.getNumeroPrenotazioni());
            dto.setTotaleCostoPulizie(totaleCostoPulizie);

            totaleNettoProprietaria =calcoloTotaleNettoProprietaria(totaleCostoTassa, totaleCostoPiattaforma, totaleLordoPernottamenti, totaleLordoGestione, totaleCostoPulizie, dto.getCostoUtenzeMensili(), dto.getMutuoAffitto());
            dto.setTotaleNettoProprietaria(totaleNettoProprietaria);

            totaleCommisioneHost = calcoloTotaleCommisioneHost(dto.getCommissioneHost(), totaleLordoPernottamenti, totaleCostoPiattaforma);
            dto.setTotaleCommissioneHost(totaleCommisioneHost);

            totaleCommisioneCoHost = calcoloTotaleCommisioneCoHost(dto.getCommissioneCoHost(),totaleLordoPernottamenti, totaleCostoPiattaforma);
            dto.setTotaleCommissioneCoHost(totaleCommisioneCoHost);

            PrevisioneGuadagno previsioneEntity;
            previsioneEntity = MappingEntity.toEntity(dto);
            previsioneEntity = pgRepository.save(previsioneEntity);

            return previsioneEntity;
        }  catch (DataIntegrityViolationException e) {
            throw new PrevisioneGuadagnoException(1002, "Errore di integrità dati nel salvataggio");
        } catch (PrevisioneGuadagnoException e) {
            throw new PrevisioneGuadagnoException(1001, e.getMessage());
        } catch (Exception e) {
            throw new PrevisioneGuadagnoException(1003, "Errore imprevisto");
        }

    }


    private static BigDecimal calcoloTotaleLordoPernottamenti(BigDecimal prezzoMedioPerNotte, int numeroNottiMensili) {
        if (prezzoMedioPerNotte == null) {
            throw new PrevisioneGuadagnoException(1001,"Prezzo medio per notte nullo");
        }
        if (numeroNottiMensili <= 0) {
            throw new PrevisioneGuadagnoException(1001, "Numero notti mensili non valido");
        }

        return prezzoMedioPerNotte.multiply(BigDecimal.valueOf(numeroNottiMensili));
    }

    private BigDecimal calcoloCostotasse(int percentualeTasse, BigDecimal totaleLordoPernottamenti) {
        if (totaleLordoPernottamenti == null) {
            throw new PrevisioneGuadagnoException(1001,"Totale lordo pernottamenti nullo");
        }

        if (percentualeTasse < 0 || percentualeTasse > 100) {
            throw new PrevisioneGuadagnoException(1001,"Percentuale tasse non valida: " + percentualeTasse);
        }

        return totaleLordoPernottamenti
                .multiply(BigDecimal.valueOf(percentualeTasse))
                .divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calcoloCostoPiattaforma(int costoPiattaforma, BigDecimal totaleLordoPernottamenti) {
        BigDecimal totaleCostoPiattaforma;
        totaleCostoPiattaforma = totaleLordoPernottamenti.multiply( BigDecimal.valueOf(costoPiattaforma)
                                                         .divide(BigDecimal.valueOf(100)));
        return totaleCostoPiattaforma;
    }

    private BigDecimal calcoloTotaleLordoGestione(int commissioneGestionetotale, BigDecimal totaleLordoPernottamenti, BigDecimal totaleCostoPiattaforma) {
        BigDecimal totaleCostoGestione;
        totaleCostoGestione= ((totaleLordoPernottamenti.subtract(totaleCostoPiattaforma))
                                                     .multiply(BigDecimal.valueOf(commissioneGestionetotale)))
                                                     .divide(BigDecimal.valueOf(100));
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

    private BigDecimal calcoloTotaleCommisioneHost(BigDecimal commissioneHost, BigDecimal totaleLordoPernottamenti, BigDecimal totaleCostoPiattaforma) {
        BigDecimal totaleCommisioneHost;
        totaleCommisioneHost = ((totaleLordoPernottamenti.subtract(totaleCostoPiattaforma))
                                                       .multiply((commissioneHost))
                                                       .divide(BigDecimal.valueOf(100)));
        return totaleCommisioneHost;
    }

    private BigDecimal calcoloTotaleCommisioneCoHost(BigDecimal commissioneCoHost, BigDecimal totaleLordoPernottamenti, BigDecimal totaleCostoPiattaforma) {
        BigDecimal totaleCommisioneCoHost;
        totaleCommisioneCoHost = ((totaleLordoPernottamenti.subtract(totaleCostoPiattaforma))
                                                           .multiply((commissioneCoHost))
                                                           .divide(BigDecimal.valueOf(100)));
        return totaleCommisioneCoHost;
    }

    private void validateDto(PrevisioneGuadagnoDto dto) {
        if (dto == null) {
            throw new PrevisioneGuadagnoException(1001,"Il body della richiesta è nullo");
        }

        if (dto.getPrezzoMedioPerNotte() == null || dto.getPrezzoMedioPerNotte().compareTo(BigDecimal.ZERO) < 0) {
            throw new PrevisioneGuadagnoException(1001,"Prezzo medio per notte non valido");
        }

        if (dto.getNumeroNottiMensili() <= 0) {
            throw new PrevisioneGuadagnoException(1001,"Numero notti mensili deve essere maggiore di 0");
        }

        if (dto.getNumeroPrenotazioni() < 0) {
            throw new PrevisioneGuadagnoException(1001,"Numero prenotazioni non può essere negativo");
        }

        if (dto.getCostoPulizia() == null || dto.getCostoPulizia().compareTo(BigDecimal.ZERO) < 0) {
            throw new PrevisioneGuadagnoException(1001,"Costo pulizia non valido");
        }

        if (dto.getCommissioneHost() != null && dto.getCommissioneHost().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new PrevisioneGuadagnoException(1001,"Commissione host non può superare il 100%");
        }
    }


}
