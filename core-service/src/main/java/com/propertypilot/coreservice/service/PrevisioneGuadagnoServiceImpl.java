package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.config.CurrentUserProvider;
import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.dto.PrevisioneGuadagnoListDto;
import com.propertypilot.coreservice.exceptionCustom.ErrorCode;
import com.propertypilot.coreservice.exceptionCustom.PrevisioneGuadagnoException;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.PrevisioneGadagnoRepository;
import com.propertypilot.coreservice.util.MappingEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrevisioneGuadagnoServiceImpl implements PrevisioneGadagnoService {

    @Autowired
    PrevisioneGadagnoRepository pgRepository;
    @Autowired
    CurrentUserProvider currentUserProvider;

    @Transactional
    @Override
    public PrevisioneGuadagno calcoloCostiPrevisioneGadagno(PrevisioneGuadagnoDto dto) {

        log.info("Calcolo previsione guadagno avviato");

        validateDto(dto);

        try {
            BigDecimal totaleLordoPernottamenti = calcolaTotaleLordoPernottamenti(dto.getPrezzoMedioPerNotte(), dto.getNumeroNottiMensili());
            dto.setTotaleLordoPernottamenti(totaleLordoPernottamenti);

            BigDecimal totaleCostoTassa = calcolaCostoTasse(dto.getCostoTasse(), totaleLordoPernottamenti);
            dto.setTotaleCostoTassa(totaleCostoTassa);

            BigDecimal totaleCostoPiattaforma = calcolaCostoPiattaforma(dto.getCostoPiattaforma(), totaleLordoPernottamenti);
            dto.setTotaleCostoPiattaforma(totaleCostoPiattaforma);

            BigDecimal totaleLordoGestione = calcolaTotaleLordoGestione(dto.getCommissioneGestioneTotale(), totaleLordoPernottamenti, totaleCostoPiattaforma);
            dto.setTotaleLordoGestione(totaleLordoGestione);

            BigDecimal totaleCostoPulizie = calcolaTotaleCostoPulizie(dto.getCostoPulizia(), dto.getNumeroPrenotazioni());
            dto.setTotaleCostoPulizie(totaleCostoPulizie);

            BigDecimal totaleNettoProprietaria = calcolaTotaleNettoProprietaria(
                    totaleCostoTassa,
                    totaleCostoPiattaforma,
                    totaleLordoPernottamenti,
                    totaleLordoGestione,
                    totaleCostoPulizie,
                    dto.getCostoUtenzeMensili(),
                    dto.getMutuoAffitto()
            );
            dto.setTotaleNettoProprietario(totaleNettoProprietaria);

            BigDecimal totaleCommissioneHost = calcolaTotaleCommissioneHost(dto.getCommissioneHost(), totaleLordoPernottamenti, totaleCostoPiattaforma);
            dto.setTotaleCommissioneHost(totaleCommissioneHost);

            BigDecimal totaleCommissioneCoHost = calcolaTotaleCommissioneCoHost(dto.getCommissioneCoHost(), totaleLordoPernottamenti, totaleCostoPiattaforma);
            dto.setTotaleCommissioneCoHost(totaleCommissioneCoHost);
            User userCreated = currentUserProvider.getCurrentUserOrThrow();
            PrevisioneGuadagno entity = MappingEntity.toEntity(dto);
            entity.setUser(userCreated);;
            entity = pgRepository.save(entity);

            log.info("Previsione guadagno salvata con ID {}", entity.getId());

            return entity;

        } catch (DataIntegrityViolationException e) {
            log.error("Errore integrità dati", e);
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Errore di integrità dati nel salvataggio");
        } catch (PrevisioneGuadagnoException e) {
            log.warn("Errore previsione guadagno: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Errore imprevisto durante il calcolo", e);
            throw new PrevisioneGuadagnoException(ErrorCode.GENERIC_ERROR, "Errore imprevisto");
        }
    }

    // ---------------------------------------------------------
    // METODI DI CALCOLO
    // ---------------------------------------------------------

    private BigDecimal calcolaTotaleLordoPernottamenti(BigDecimal prezzoMedio, int notti) {
        if (prezzoMedio == null || prezzoMedio.compareTo(BigDecimal.ZERO) < 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Prezzo medio per notte non valido");

        if (notti <= 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Numero notti mensili non valido");

        return prezzoMedio.multiply(BigDecimal.valueOf(notti));
    }

    private BigDecimal calcolaCostoTasse(int percentuale, BigDecimal totaleLordo) {
        if (percentuale < 0 || percentuale > 100)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Percentuale tasse non valida");

        return totaleLordo.multiply(BigDecimal.valueOf(percentuale)).divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calcolaCostoPiattaforma(int percentuale, BigDecimal totaleLordo) {
        return totaleLordo.multiply(BigDecimal.valueOf(percentuale)).divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calcolaTotaleLordoGestione(int percentuale, BigDecimal totaleLordo, BigDecimal costoPiattaforma) {
        return totaleLordo.subtract(costoPiattaforma)
                .multiply(BigDecimal.valueOf(percentuale))
                .divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calcolaTotaleCostoPulizie(BigDecimal costoPulizia, int prenotazioni) {
        return costoPulizia.multiply(BigDecimal.valueOf(prenotazioni));
    }

    private BigDecimal calcolaTotaleNettoProprietaria(
            BigDecimal tasse,
            BigDecimal piattaforma,
            BigDecimal lordo,
            BigDecimal gestione,
            BigDecimal pulizie,
            BigDecimal utenze,
            BigDecimal mutuo
    ) {
        return lordo
                .subtract(piattaforma)
                .subtract(tasse)
                .subtract(gestione)
                .subtract(pulizie)
                .subtract(utenze)
                .subtract(mutuo);
    }

    private BigDecimal calcolaTotaleCommissioneHost(BigDecimal percentuale, BigDecimal lordo, BigDecimal piattaforma) {
        return lordo.subtract(piattaforma)
                .multiply(percentuale)
                .divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calcolaTotaleCommissioneCoHost(BigDecimal percentuale, BigDecimal lordo, BigDecimal piattaforma) {
        return lordo.subtract(piattaforma)
                .multiply(percentuale)
                .divide(BigDecimal.valueOf(100));
    }

    // ---------------------------------------------------------
    // VALIDAZIONE DTO
    // ---------------------------------------------------------

    private void validateDto(PrevisioneGuadagnoDto dto) {
        if (dto == null)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Il body della richiesta è nullo");

        if (dto.getPrezzoMedioPerNotte() == null || dto.getPrezzoMedioPerNotte().compareTo(BigDecimal.ZERO) < 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Prezzo medio per notte non valido");

        if (dto.getNumeroNottiMensili() <= 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Numero notti mensili deve essere maggiore di 0");

        if (dto.getNumeroPrenotazioni() < 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Numero prenotazioni non può essere negativo");

        if (dto.getCostoPulizia() == null || dto.getCostoPulizia().compareTo(BigDecimal.ZERO) < 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Costo pulizia non valido");

        if (dto.getCommissioneHost() != null && dto.getCommissioneHost().compareTo(BigDecimal.valueOf(100)) > 0)
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "Commissione host non può superare il 100%");
    }
    @Override
    public List<PrevisioneGuadagnoListDto> getAllPrevisioni() {
        User user = currentUserProvider.getCurrentUserOrThrow();
        log.info("Get lista previsioni - userId={}", user.getId());

        return pgRepository.findAllByUserOrderByIdDesc(user)
                .stream()
                .map(PrevisioneGuadagnoListDto::fromEntity)
                .toList();
    }

    @Override
    public PrevisioneGuadagnoDto getDettaglioById(Long previsioneId) {
        User user = currentUserProvider.getCurrentUserOrThrow();
        log.info("Get dettaglio previsione - id={} userId={}", previsioneId, user.getId());

        PrevisioneGuadagno entity = pgRepository.findByIdAndUser(previsioneId, user)
                .orElseThrow(() -> {
                    log.warn("Previsione non trovata o non accessibile - id={} userId={}", previsioneId, user.getId());
                    return new PrevisioneGuadagnoException(ErrorCode.PREVISIONE_NOT_FOUND, "Previsione non trovata");
                });

        return MappingEntity.toDto(entity);
    }
    @Override
    public void deleteById(Long previsioneId) {
        User user = currentUserProvider.getCurrentUserOrThrow();

        PrevisioneGuadagno entity = pgRepository.findByIdAndUser(previsioneId, user)
                .orElseThrow(() -> new PrevisioneGuadagnoException(
                        ErrorCode.PREVISIONE_NOT_FOUND,
                        "Previsione non trovata o non accessibile"
                ));

        pgRepository.delete(entity);

        log.info("Previsione eliminata id={} userId={}", previsioneId, user.getId());
    }
}