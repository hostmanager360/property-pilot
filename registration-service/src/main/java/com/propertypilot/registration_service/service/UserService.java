package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.UserDto;

public interface UserService {
    public User registerUser(UserDto dto);
}
