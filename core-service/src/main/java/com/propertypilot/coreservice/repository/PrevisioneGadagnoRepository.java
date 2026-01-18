package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrevisioneGadagnoRepository extends JpaRepository<PrevisioneGuadagno, Integer> {
}
