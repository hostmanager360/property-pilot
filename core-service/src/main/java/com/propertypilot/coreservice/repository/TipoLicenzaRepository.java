package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.TipoLicenza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoLicenzaRepository extends JpaRepository<TipoLicenza, Integer> {

    Optional<TipoLicenza> findByCodiceLicenza(String codiceLicenza);
}