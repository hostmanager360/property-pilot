package com.propertypilot.service;

import com.propertypilot.model.DettagliAppartamento;

import java.util.List;
import java.util.Optional;

public interface DettagliAppartamentoService {
    List<DettagliAppartamento> findAll();
    Optional<DettagliAppartamento> findById(Integer id);
    DettagliAppartamento save(DettagliAppartamento dettagliAppartamento);
    void deleteById(Integer id);
}
