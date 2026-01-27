package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.dto.FirstAccessStatusResponse;
import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.model.User;

public interface FirstAccessService {
    FirstAccessStatusResponse getStatus();
    void createTenant(CreateTenantDTO dto);
    void completeUserDetail(UserDetailDto request);
    void completeFirstAccess();
    void updateStep(User user, String stepCode);
}
