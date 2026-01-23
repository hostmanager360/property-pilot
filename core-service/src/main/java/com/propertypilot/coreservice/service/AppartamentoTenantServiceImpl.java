package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.CreateTenantAppartamentoDTO;
import com.propertypilot.coreservice.model.Appartamento;
import com.propertypilot.coreservice.model.AppartamentoTenant;
import com.propertypilot.coreservice.repository.AppartamentiRepository;
import com.propertypilot.coreservice.repository.AppartamentoTenantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppartamentoTenantServiceImpl implements AppartamentoTenantService {

    @Autowired
    AppartamentoTenantRepository appartamentoTenantRepository;
    @Autowired
    AppartamentiRepository appartamentiRepository;

    @Override
    public AppartamentoTenant createTenant(CreateTenantAppartamentoDTO dto) {
        Appartamento appartamento = appartamentiRepository.findById(dto.getAppartamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Appartamento non trovato"));

        AppartamentoTenant tenant = AppartamentoTenant.builder()
                .appartamento(appartamento)
                .tenantKey(dto.getTenantKey())
                .build();

        return appartamentoTenantRepository.save(tenant);
    }

}
