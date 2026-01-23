package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.AppartamentoTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppartamentoTenantRepository extends JpaRepository<AppartamentoTenant, Long> {
    Optional<AppartamentoTenant> findByAppartamentoId(Long appartamentoId);
}
