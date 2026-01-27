package com.propertypilot.registration_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private String confermaPassword;
    private String role;       // per register base
    private String tenantKey;  // opzionale per admin creato da OWNER
}

