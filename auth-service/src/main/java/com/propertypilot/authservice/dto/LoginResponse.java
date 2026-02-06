package com.propertypilot.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private String tenantKey;
    private long role;
    private String email;
    private String resetPasswordToken;
    private boolean passwordResetRequired;
    private boolean firstAccessRequired;
    private long firstAccessStep;
}
