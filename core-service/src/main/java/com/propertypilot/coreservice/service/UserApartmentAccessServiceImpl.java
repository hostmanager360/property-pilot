package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.context.TenantContext;
import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.exceptionCustom.ApartmentAlreadyAssignedException;
import com.propertypilot.coreservice.exceptionCustom.ApartmentNotFoundException;
import com.propertypilot.coreservice.exceptionCustom.UnauthorizedAccessException;
import com.propertypilot.coreservice.exceptionCustom.UserNotFoundException;
import com.propertypilot.coreservice.model.*;
import com.propertypilot.coreservice.repository.*;
import com.propertypilot.coreservice.util.AppartamentoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserApartmentAccessServiceImpl implements UserApartmentAccessService {

    @Autowired
    UserApartmentAccessRepository accessRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AppartamentiRepository apartmentRepository;
    @Autowired
    AppartamentoTenantRepository appartamentoTenantRepository;
    @Autowired
    AppartamentoMapper appartamentoMapper;
    @Autowired
    DettagliAppartamentoRepository dettagliRepository;


    // ---------------------------------------------------------
    // UTILITY: VALIDAZIONE TENANT SU USER
    // ---------------------------------------------------------
    private User validateUserTenant(long userId, String tenantKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utente non trovato."));

        if (!user.getTenantKey().equals(tenantKey)) {
            throw new UnauthorizedAccessException("L'utente non appartiene al tenant corrente.");
        }

        return user;
    }

    // ---------------------------------------------------------
    // UTILITY: VALIDAZIONE TENANT SU APPARTAMENTO
    // ---------------------------------------------------------
    private Appartamento validateApartmentTenant(Integer apartmentId, String tenantKey) {

        Appartamento apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException("Appartamento non trovato."));

        AppartamentoTenant appartamentoTenant = appartamentoTenantRepository
                .findByAppartamentoId(apartmentId)
                .orElseThrow(() -> new UnauthorizedAccessException("Associazione appartamento-tenant non trovata."));

        if (!appartamentoTenant.getTenantKey().equals(tenantKey)) {
            throw new UnauthorizedAccessException("L'appartamento non appartiene al tenant corrente.");
        }

        return apartment;
    }

    // ---------------------------------------------------------
    // ASSEGNAZIONE SINGOLA
    // ---------------------------------------------------------
    @Override
    public UserApartmentAccess assignApartmentToUser(long userId, Integer apartmentId) {

        String tenantKey = TenantContext.getTenant();

        User user = validateUserTenant(userId, tenantKey);
        Appartamento apartment = validateApartmentTenant(apartmentId, tenantKey);

        if (accessRepository.existsByUserIdAndApartmentId(userId, apartmentId)) {
            throw new ApartmentAlreadyAssignedException("L'appartamento è già assegnato a questo utente.");
        }

        UserApartmentAccess access = new UserApartmentAccess();
        access.setUser(user);
        access.setApartment(apartment);

        return accessRepository.save(access);
    }

    // ---------------------------------------------------------
    // RIMOZIONE SINGOLA
    // ---------------------------------------------------------
    @Override
    public void removeApartmentFromUser(long userId, Integer apartmentId) {

        String tenantKey = TenantContext.getTenant();

        validateUserTenant(userId, tenantKey);
        validateApartmentTenant(apartmentId, tenantKey);

        accessRepository.deleteByUserIdAndApartmentId(userId, apartmentId);
    }

    // ---------------------------------------------------------
    // GET APPARTAMENTI PER USER
    // ---------------------------------------------------------
    @Override
    public List<UserApartmentAccess> getApartmentsForUser(long userId) {

        String tenantKey = TenantContext.getTenant();
        validateUserTenant(userId, tenantKey);

        return accessRepository.findByUserId(userId);
    }

    // ---------------------------------------------------------
    // ASSEGNAZIONE MULTIPLA
    // ---------------------------------------------------------
    @Override
    public List<UserApartmentAccess> assignMultiple(Long userId, List<Integer> apartmentIds) {

        String tenantKey = TenantContext.getTenant();
        User user = validateUserTenant(userId, tenantKey);

        List<UserApartmentAccess> created = new ArrayList<>();

        for (Integer apartmentId : apartmentIds) {

            if (accessRepository.existsByUserIdAndApartmentId(userId, apartmentId)) {
                continue;
            }

            Appartamento apartment = validateApartmentTenant(apartmentId, tenantKey);

            UserApartmentAccess access = new UserApartmentAccess();
            access.setUser(user);
            access.setApartment(apartment);

            created.add(accessRepository.save(access));
        }

        return created;
    }

    // ---------------------------------------------------------
    // RIMOZIONE TUTTI GLI APPARTAMENTI
    // ---------------------------------------------------------
    @Override
    public void removeAllForUser(Long userId) {

        String tenantKey = TenantContext.getTenant();
        validateUserTenant(userId, tenantKey);

        accessRepository.deleteAllByUserId(userId);
    }

    // ---------------------------------------------------------
    // SOLO ID APPARTAMENTI
    // ---------------------------------------------------------
    @Override
    public List<Integer> getApartmentIds(Long userId) {

        String tenantKey = TenantContext.getTenant();
        validateUserTenant(userId, tenantKey);

        return accessRepository.findApartmentIdsByUserId(userId);
    }
    @Override
    public List<AppartamentoDettagliDTO> getAppartamentiDettagliByUser(Long userId) {

        String tenantKey = TenantContext.getTenant();
        validateUserTenant(userId, tenantKey);

        List<UserApartmentAccess> accessList = accessRepository.findByUserIdWithApartment(userId);

        List<AppartamentoDettagliDTO> result = new ArrayList<>();

        for (UserApartmentAccess access : accessList) {

            Appartamento appartamento = access.getApartment();

            validateApartmentTenant(appartamento.getId(), tenantKey);

            DettagliAppartamento dettagli =
                    dettagliRepository.findByAppartamentoId(appartamento.getId()).orElse(null);

            result.add(appartamentoMapper.toDTO(appartamento, dettagli));
        }

        return result;
    }

}
