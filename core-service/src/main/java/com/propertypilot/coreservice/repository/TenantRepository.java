package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
}