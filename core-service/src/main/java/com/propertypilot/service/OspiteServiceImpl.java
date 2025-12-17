package com.propertypilot.service;

import com.propertypilot.model.Ospite;
import com.propertypilot.repository.OspiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OspiteServiceImpl implements OspiteService {

    private final OspiteRepository repository;

    public OspiteServiceImpl(OspiteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Ospite> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Ospite> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Ospite save(Ospite ospite) {
        return repository.save(ospite);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}