package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.model.ForgotPasswordRequestDTO;
import com.propertypilot.registration_service.model.ResetPasswordRequestDTO;
import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.UserDto;

public interface UserService {
    public User registerUser(UserDto dto);

    void forgotPassword(ForgotPasswordRequestDTO dto);

    void resetPassword(ResetPasswordRequestDTO dto);

    User createAdmin(UserDto dto, String tenantKey);

    User createHost(UserDto dto, String tenantKey);

    User createCohost(UserDto dto, String tenantKey);
}
