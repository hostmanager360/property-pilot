package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.model.StatusTenant;

import java.util.List;
import java.util.Optional;

public interface StatusTenantService {
    List<StatusTenant> findAll();

    Optional<StatusTenant> findById(Integer id);

    Optional<StatusTenant> findByStatusCode(String statusCode);

    StatusTenant save(StatusTenant statusTenant);

    void deleteById(Integer id);
}
