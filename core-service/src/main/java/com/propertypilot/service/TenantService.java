package com.propertypilot.service;

import com.propertypilot.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    List<Tenant> findAll();

    Optional<Tenant> findByKey(String keyTenant);

    Tenant save(Tenant tenant);

    void deleteByKey(String keyTenant);
}

