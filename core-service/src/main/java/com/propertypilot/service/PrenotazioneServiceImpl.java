package com.propertypilot.service;

import com.propertypilot.model.Prenotazione;
import com.propertypilot.repository.PrenotazioneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final PrenotazioneRepository repository;

    public PrenotazioneServiceImpl(PrenotazioneRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Prenotazione> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Prenotazione> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Prenotazione save(Prenotazione prenotazione) {
        return repository.save(prenotazione);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}