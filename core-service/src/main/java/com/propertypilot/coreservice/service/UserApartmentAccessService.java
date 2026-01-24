package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.model.UserApartmentAccess;

import java.util.List;

public interface UserApartmentAccessService {
    UserApartmentAccess assignApartmentToUser(long userId, Integer apartmentId);

    void removeApartmentFromUser(long userId, Integer apartmentId);

    List<UserApartmentAccess> getApartmentsForUser(long userId);

    List<UserApartmentAccess> assignMultiple(Long userId, List<Integer> apartmentIds);

    void removeAllForUser(Long userId);

    List<Integer> getApartmentIds(Long userId);

    List<AppartamentoDettagliDTO> getAppartamentiDettagliByUser(Long userId);
}


