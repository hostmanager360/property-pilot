package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.context.TenantContext;
import com.propertypilot.coreservice.dto.CreateAppartamentoDTO;
import com.propertypilot.coreservice.model.Appartamento;
import com.propertypilot.coreservice.model.AppartamentoTenant;
import com.propertypilot.coreservice.repository.AppartamentiRepository;
import com.propertypilot.coreservice.repository.AppartamentoTenantRepository;
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

    @Transactional
    public Appartamento createAppartamento(CreateAppartamentoDTO dto) {

        String tenantKey = TenantContext.getTenant();
        if (tenantKey == null) {
            throw new RuntimeException("Tenant non trovato nel contesto");
        }

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

        AppartamentoTenant link = new AppartamentoTenant();
        link.setTenantKey(tenantKey);
        link.setAppartamento(saved);
        tenantAppartamentoRepository.save(link);

        return saved;
    }

}
