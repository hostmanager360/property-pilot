package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.context.TenantContext;
import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.dto.CreateAppartamentoDTO;
import com.propertypilot.coreservice.model.Appartamento;
import com.propertypilot.coreservice.model.AppartamentoTenant;
import com.propertypilot.coreservice.model.DettagliAppartamento;
import com.propertypilot.coreservice.repository.AppartamentiRepository;
import com.propertypilot.coreservice.repository.AppartamentoTenantRepository;
import com.propertypilot.coreservice.repository.DettagliAppartamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppartamentoServiceImpl implements AppartamentoService {
    @Override
    public List<Appartamento> findAll() {
        return List.of();
    }

    @Override
    public List<Appartamento> findById(Integer id) {
        return List.of();
    }

    @Override
    public Appartamento save(Appartamento appartamento) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Autowired
    private AppartamentiRepository appartamentoRepository;

    @Autowired
    private AppartamentoTenantRepository tenantAppartamentoRepository;
    @Autowired
    DettagliAppartamentoRepository dettagliAppartamentoRepository
            ;
    @Transactional
    public AppartamentoDettagliDTO createAppartamento(AppartamentoDettagliDTO dto) {

        String tenantKey = TenantContext.getTenant();
        if (tenantKey == null) {
            throw new RuntimeException("Tenant non trovato nel contesto");
        }

        // 1️⃣ Mappa DTO → Appartamento
        Appartamento appartamento = new Appartamento();
        appartamento.setNomeAppartamento(dto.getNomeAppartamento());
        appartamento.setVia(dto.getVia());
        appartamento.setCivico(dto.getCivico());
        appartamento.setCitta(dto.getCitta());
        appartamento.setStato(dto.getStato());
        appartamento.setCup(dto.getCup());
        appartamento.setCin(dto.getCin());
        appartamento.setCir(dto.getCir());
        appartamento.setCreatedAt(LocalDateTime.now());
        appartamento.setUpdateAt(LocalDateTime.now());

        Appartamento saved = appartamentoRepository.save(appartamento);

        // 2️⃣ Mappa DTO → DettagliAppartamento
        DettagliAppartamento dettagli = new DettagliAppartamento();
        dettagli.setAppartamento(saved);
        dettagli.setDataInizioAttivita(dto.getDataInizioAttivita());
        dettagli.setCostoPulizie(dto.getCostoPulizie());
        dettagli.setCostoUtenzeGiornaliere(dto.getCostoUtenzeGiornaliere());
        dettagli.setCostoTassaSoggiornoPerOspite(dto.getCostoTassaSoggiornoPerOspite());
        dettagli.setTassaSoggiornoAutomatica(dto.isTassaSoggiornoAutomatica());
        dettagli.setCedolareSecca(dto.isCedolareSecca());
        dettagli.setPercentualeCommissionePm(dto.getPercentualeCommissionePm());
        dettagli.setPercentualeCommissioneHost(dto.getPercentualeCommissioneHost());
        dettagli.setPercentualeCommissioneTotale(dto.getPercentualeCommissioneTotale());
        dettagli.setCreatedAt(LocalDateTime.now());
        dettagli.setUpdateAt(LocalDateTime.now());

        dettagliAppartamentoRepository.save(dettagli);

        // 3️⃣ Crea link tenant ↔ appartamento
        AppartamentoTenant link = new AppartamentoTenant();
        link.setTenantKey(tenantKey);
        link.setAppartamento(saved);
        tenantAppartamentoRepository.save(link);

        // 4️⃣ Ritorna DTO al frontend
        return AppartamentoDettagliDTO.builder()
                .id(saved.getId())
                .nomeAppartamento(saved.getNomeAppartamento())
                .via(saved.getVia())
                .civico(saved.getCivico())
                .citta(saved.getCitta())
                .stato(saved.getStato())
                .cup(saved.getCup())
                .cin(saved.getCin())
                .cir(saved.getCir())
                .dataInizioAttivita(dettagli.getDataInizioAttivita())
                .costoPulizie(dettagli.getCostoPulizie())
                .costoUtenzeGiornaliere(dettagli.getCostoUtenzeGiornaliere())
                .costoTassaSoggiornoPerOspite(dettagli.getCostoTassaSoggiornoPerOspite())
                .tassaSoggiornoAutomatica(dettagli.isTassaSoggiornoAutomatica())
                .cedolareSecca(dettagli.isCedolareSecca())
                .percentualeCommissionePm(dettagli.getPercentualeCommissionePm())
                .percentualeCommissioneHost(dettagli.getPercentualeCommissioneHost())
                .percentualeCommissioneTotale(dettagli.getPercentualeCommissioneTotale())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdateAt())
                .build();
    }


}
