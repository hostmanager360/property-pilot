package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.model.Prenotazione;
import com.propertypilot.coreservice.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface PrenotazioneService {
    List<Prenotazione> findAll();
    List<Prenotazione> findAllByTenant(Tenant tenant);
    Optional<Prenotazione> findById(Integer id);
    Prenotazione save(Prenotazione prenotazione);
    void deleteById(Integer id);
}
