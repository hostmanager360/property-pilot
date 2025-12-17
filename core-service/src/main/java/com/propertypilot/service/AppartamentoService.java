package com.propertypilot.service;

import com.propertypilot.model.Appartamento;

import java.util.List;

public interface AppartamentoService {

    List<Appartamento> findAll();
    List<Appartamento> findById(Integer id);
    Appartamento save(Appartamento appartamento);
    void deleteById(Integer id);
}
