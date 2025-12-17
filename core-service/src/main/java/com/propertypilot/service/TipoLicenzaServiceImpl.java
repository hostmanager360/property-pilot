package com.propertypilot.service;


import com.propertypilot.model.TipoLicenza;
import com.propertypilot.repository.TipoLicenzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoLicenzaServiceImpl implements TipoLicenzaService {

    private final TipoLicenzaRepository repository;

    public TipoLicenzaServiceImpl(TipoLicenzaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TipoLicenza> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<TipoLicenza> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<TipoLicenza> findByCodiceLicenza(String codiceLicenza) {
        return repository.findByCodiceLicenza(codiceLicenza);
    }

    @Override
    public TipoLicenza save(TipoLicenza tipoLicenza) {
        return repository.save(tipoLicenza);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}