package com.propertypilot.coreservice.repository;

import com.propertypilot.coreservice.model.UserApartmentAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserApartmentAccessRepository extends JpaRepository<UserApartmentAccess, Long> {

    List<UserApartmentAccess> findByUserId(long userId);

    boolean existsByUserIdAndApartmentId(long userId, Integer apartmentId);

    void deleteByUserIdAndApartmentId(long userId, Integer apartmentId);

    void deleteAllByUserId(Long userId);

    @Query("SELECT uaa.apartment.id FROM UserApartmentAccess uaa WHERE uaa.user.id = :userId")
    List<Integer> findApartmentIdsByUserId(Long userId);

}
