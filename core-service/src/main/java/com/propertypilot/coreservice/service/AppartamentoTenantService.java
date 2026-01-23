package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.CreateTenantAppartamentoDTO;
import com.propertypilot.coreservice.model.AppartamentoTenant;

public interface AppartamentoTenantService {
    public AppartamentoTenant createTenant(CreateTenantAppartamentoDTO dto);
}
