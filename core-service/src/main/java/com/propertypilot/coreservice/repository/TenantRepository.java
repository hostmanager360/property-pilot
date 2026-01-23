package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.Tenant;
import com.propertypilot.coreservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query("SELECT t.keyTenant FROM Tenant t ORDER BY t.keyTenant DESC LIMIT 1")
    String findLastKeyTenant();
}