package com.propertypilot.registration_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String token;
    private String newPassword;
    private String repeatPassword;
}
