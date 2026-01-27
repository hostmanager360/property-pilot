package com.propertypilot.registration_service.repository;

import com.propertypilot.registration_service.model.FirstAccessStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FirstAccessStepRepository extends JpaRepository<FirstAccessStep, Long> {
    Optional<FirstAccessStep> findByCode(String code);
}
