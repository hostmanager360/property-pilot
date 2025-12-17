package com.propertypilot.repository;

import com.propertypilot.model.DettagliAppartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DettagliAppartamentoRepository extends JpaRepository<DettagliAppartamento, Integer> {
}

