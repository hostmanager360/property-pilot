package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {

    Optional<UserDetail> findByUser(User user);
    Optional<UserDetail> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

}

