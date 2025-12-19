package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.model.TipoLicenza;

import java.util.List;
import java.util.Optional;

public interface TipoLicenzaService {

    List<TipoLicenza> findAll();

    Optional<TipoLicenza> findById(Integer id);

    Optional<TipoLicenza> findByCodiceLicenza(String codiceLicenza);

    TipoLicenza save(TipoLicenza tipoLicenza);

    void deleteById(Integer id);
}