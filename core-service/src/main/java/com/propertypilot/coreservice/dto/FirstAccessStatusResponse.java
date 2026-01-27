package com.propertypilot.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirstAccessStatusResponse {
    private String nextStep;              // CREATE_TENANT, COMPLETE_USER_DETAIL, DASHBOARD
    private String role;
    private String tenantKey;
    private boolean firstAccessCompleted;
}
