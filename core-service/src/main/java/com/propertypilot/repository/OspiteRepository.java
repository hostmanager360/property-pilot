package com.propertypilot.repository;

import com.propertypilot.model.Ospite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OspiteRepository  extends JpaRepository<Ospite, Integer> {
}
