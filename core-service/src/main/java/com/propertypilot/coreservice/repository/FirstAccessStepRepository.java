package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.FirstAccessStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FirstAccessStepRepository extends JpaRepository<FirstAccessStep, Long> {
    Optional<FirstAccessStep> findByCode(String code);
}
