package com.propertypilot.repository;

import com.propertypilot.model.TipoGestione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoGestioneRepository extends JpaRepository<TipoGestione, Integer> {

    Optional<TipoGestione> findByCodGestione(String codGestione);
}