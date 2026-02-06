package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.config.CurrentUserProvider;
import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.dto.FirstAccessStatusResponse;
import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.exceptionCustom.ForbiddenException;
import com.propertypilot.coreservice.model.FirstAccessStep;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.FirstAccessStepRepository;
import com.propertypilot.coreservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirstAccessServiceImpl implements FirstAccessService {

    @Autowired
    CurrentUserProvider currentUserProvider;
    @Autowired
    UserDetailService userDetailService;
    @Autowired
    TenantService tenantService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FirstAccessStepRepository firstAccessStepRepository;

    @Override
    public FirstAccessStatusResponse getStatus() {

        User user = currentUserProvider.getCurrentUserOrThrow();

        return new FirstAccessStatusResponse(
                user.getFirstAccessStep().getCode(),
                user.getRoleEntity().getCode(),
                user.getTenantKey(),
                Boolean.TRUE.equals(user.getFirstAccessCompleted())
        );
    }

    @Override
    @Transactional
    public void createTenant(CreateTenantDTO dto) {
        User user = currentUserProvider.getCurrentUserOrThrow();

        if (!"ADMIN".equals(user.getRoleEntity().getCode())) {
            throw new ForbiddenException("Solo ADMIN può creare un tenant al primo accesso");
        }

        if (user.getTenantKey() != null) {
            throw new ForbiddenException("Tenant già associato all'utente");
        }

        tenantService.createTenant(user, dto);
        if(dto.getTipoSoggetto() != null && "PERSONA_FISICA".equalsIgnoreCase(dto.getTipoSoggetto())){
            updateStep(user, "COMPLETE_USER_DETAIL");
        } else {
            updateStep(user, "DASHBOARD");
        }
    }


    @Override
    @Transactional
    public void completeUserDetail(UserDetailDto dto) {

        User user = currentUserProvider.getCurrentUserOrThrow();

        userDetailService.saveOrUpdate(user, dto);
        user.setFirstAccessCompleted(true);
        updateStep(user, "DASHBOARD");
    }

    @Override
    @Transactional
    public void completeFirstAccess() {

        User user = currentUserProvider.getCurrentUserOrThrow();

        if (!"DASHBOARD".equals(user.getFirstAccessStep().getCode())) {
            throw new ForbiddenException("Non hai completato tutti i passaggi del primo accesso");
        }

        user.setFirstAccessCompleted(true);
        userRepository.save(user);
    }

    @Override
    public void updateStep(User user, String stepCode) {
        FirstAccessStep step = firstAccessStepRepository.findByCode(stepCode)
                .orElseThrow(() -> new RuntimeException("Step non trovato: " + stepCode));

        user.setFirstAccessStep(step);
        userRepository.save(user);
    }

}
