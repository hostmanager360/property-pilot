package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.Ospite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OspiteRepository  extends JpaRepository<Ospite, Integer> {
}
