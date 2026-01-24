package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByIdAndTenantKeyIsNotNull(long id);

}