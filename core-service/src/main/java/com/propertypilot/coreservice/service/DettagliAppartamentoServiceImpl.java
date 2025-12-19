package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.model.DettagliAppartamento;
import com.propertypilot.coreservice.repository.DettagliAppartamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DettagliAppartamentoServiceImpl implements DettagliAppartamentoService {

    private final DettagliAppartamentoRepository repository;

    public DettagliAppartamentoServiceImpl(DettagliAppartamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DettagliAppartamento> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<DettagliAppartamento> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public DettagliAppartamento save(DettagliAppartamento dettagliAppartamento) {
        return repository.save(dettagliAppartamento);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}