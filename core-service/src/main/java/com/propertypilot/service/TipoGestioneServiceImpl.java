package com.propertypilot.service;

import com.propertypilot.model.TipoGestione;
import com.propertypilot.repository.TipoGestioneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoGestioneServiceImpl implements TipoGestioneService {

    private final TipoGestioneRepository repository;

    public TipoGestioneServiceImpl(TipoGestioneRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TipoGestione> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<TipoGestione> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<TipoGestione> findByCodGestione(String codGestione) {
        return repository.findByCodGestione(codGestione);
    }

    @Override
    public TipoGestione save(TipoGestione tipoGestione) {
        return repository.save(tipoGestione);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}