package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.exceptionCustom.RoleException;
import com.propertypilot.coreservice.model.Role;
import com.propertypilot.coreservice.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        log.info("Richiesta caricamento ruoli");

        List<Role> roles = roleRepository.findAll();

        if (roles.isEmpty()) {
            log.warn("Nessun ruolo trovato nel database");
            throw new RoleException("Nessun ruolo disponibile");
        }

        log.info("Ruoli caricati: {}", roles.size());
        return roles;
    }

}