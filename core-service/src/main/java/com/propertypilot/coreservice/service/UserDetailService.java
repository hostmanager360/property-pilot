package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.model.User;

public interface UserDetailService{
    void saveOrUpdate(User user, UserDetailDto dto);
    boolean existsForUser(User user);
}
