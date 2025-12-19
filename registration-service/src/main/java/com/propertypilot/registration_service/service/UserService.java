package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.model.User;

public interface UserService {
    public User registerUser(String email, String rawPassword, String role);
    public void enableUser(String email);
}
