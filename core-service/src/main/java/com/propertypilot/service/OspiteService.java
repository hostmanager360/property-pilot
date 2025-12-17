package com.propertypilot.service;

import com.propertypilot.model.Ospite;

import java.util.List;
import java.util.Optional;

public interface OspiteService {
    List<Ospite> findAll();
    Optional<Ospite> findById(Integer id);
    Ospite save(Ospite ospite);
    void deleteById(Integer id);
}
