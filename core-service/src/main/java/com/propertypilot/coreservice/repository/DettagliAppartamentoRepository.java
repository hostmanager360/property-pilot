package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.DettagliAppartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DettagliAppartamentoRepository extends JpaRepository<DettagliAppartamento, Integer> {
    Optional<DettagliAppartamento> findByAppartamentoId(Integer appartamentoId);

}

