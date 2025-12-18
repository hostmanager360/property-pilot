package com.propertypilot.service;

import com.propertypilot.exceptionCustom.PrenotazioneException;
import com.propertypilot.model.Prenotazione;
import com.propertypilot.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneServiceImpl implements PrenotazioneService {

    @Autowired
    PrenotazioneRepository prenotazioneRepository;

    @Override
    public List<Prenotazione> findAll() {
        return prenotazioneRepository.findAll();
    }

    @Override
    public List<Prenotazione> findAllByTenant(String tenant) {
        List<Prenotazione> prenotazioni =
                prenotazioneRepository.findByTenant(tenant);

        if (prenotazioni.isEmpty()) {
            throw new PrenotazioneException(
                    1001,
                    "Nessuna prenotazione trovata per il tenant " + tenant
            );
        }
        return prenotazioni;
    }

    @Override
    public Optional<Prenotazione> findById(Integer id) {
        return prenotazioneRepository.findById(id);
    }

    @Override
    public Prenotazione save(Prenotazione prenotazione) {
        return prenotazioneRepository.save(prenotazione);
    }

    @Override
    public void deleteById(Integer id) {
        prenotazioneRepository.deleteById(id);
    }
}