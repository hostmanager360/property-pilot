package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    List<Tenant> findAll();

    Optional<Tenant> findByKey(String keyTenant);

    Tenant save(Tenant tenant);

    void deleteByKey(String keyTenant);

    public Tenant createTenant(CreateTenantDTO dto);
}

