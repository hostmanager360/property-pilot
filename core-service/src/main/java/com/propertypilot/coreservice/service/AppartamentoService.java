package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.dto.CreateAppartamentoDTO;
import com.propertypilot.coreservice.model.Appartamento;
import com.propertypilot.coreservice.model.UserApartmentAccess;

import java.util.List;

public interface AppartamentoService {

    List<Appartamento> findAll();
    List<Appartamento> findById(Integer id);
    Appartamento save(Appartamento appartamento);
    void deleteById(Integer id);
    AppartamentoDettagliDTO createAppartamento(AppartamentoDettagliDTO dto);
}
