package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.Prenotazione;
import com.propertypilot.coreservice.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {
    List<Prenotazione> findByTenant(Tenant tenantKey);
}
