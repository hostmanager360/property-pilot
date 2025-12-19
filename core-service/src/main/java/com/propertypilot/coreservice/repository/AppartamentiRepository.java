package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.Appartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppartamentiRepository extends JpaRepository<Appartamento, Integer> {
}
