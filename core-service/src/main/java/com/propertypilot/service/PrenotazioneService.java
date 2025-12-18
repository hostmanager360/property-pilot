package com.propertypilot.service;

import com.propertypilot.model.Prenotazione;

import java.util.List;
import java.util.Optional;

public interface PrenotazioneService {
    List<Prenotazione> findAll();
    List<Prenotazione> findAllByTenant(String tenant);
    Optional<Prenotazione> findById(Integer id);
    Prenotazione save(Prenotazione prenotazione);
    void deleteById(Integer id);
}
