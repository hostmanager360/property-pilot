package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.model.TipoGestione;

import java.util.List;
import java.util.Optional;

public interface TipoGestioneService {
    List<TipoGestione> findAll();

    Optional<TipoGestione> findById(Integer id);

    Optional<TipoGestione> findByCodGestione(String codGestione);

    TipoGestione save(TipoGestione tipoGestione);

    void deleteById(Integer id);
}
