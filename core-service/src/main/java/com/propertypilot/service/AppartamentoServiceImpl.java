package com.propertypilot.service;

import com.propertypilot.model.Appartamento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppartamentoServiceImpl implements AppartamentoService {
    @Override
    public List<Appartamento> findAll() {
        return List.of();
    }

    @Override
    public List<Appartamento> findById(Integer id) {
        return List.of();
    }

    @Override
    public Appartamento save(Appartamento appartamento) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }
}
