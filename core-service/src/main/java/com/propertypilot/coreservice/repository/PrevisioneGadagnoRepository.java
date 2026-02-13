package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrevisioneGadagnoRepository extends JpaRepository<PrevisioneGuadagno, Integer> {
    List<PrevisioneGuadagno> findAllByUserOrderByIdDesc(User user);
    Optional<PrevisioneGuadagno> findByIdAndUser(Long id, User user);
}
