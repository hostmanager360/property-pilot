package com.propertypilot.repository;

import com.propertypilot.model.StatusTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusTenantRepository extends JpaRepository<StatusTenant, Integer> {

    Optional<StatusTenant> findByStatusCode(String statusCode);
}