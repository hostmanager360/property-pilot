package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.model.UserDetail;
import com.propertypilot.coreservice.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailService{

    @Autowired
    UserDetailRepository userDetailRepository;

    @Transactional
    @Override
    public void saveOrUpdate(User user, UserDetailDto dto) {

        UserDetail detail = userDetailRepository
                .findByUserId(user.getId())
                .orElseGet(() -> {
                    UserDetail ud = new UserDetail();
                    ud.setUser(user);
                    return ud;
                });

        detail.setNome(dto.getNome());
        detail.setCognome(dto.getCognome());
        detail.setTelefono(dto.getTelefono());
        detail.setDataNascita(dto.getDataNascita());

        userDetailRepository.save(detail);
    }

    public boolean existsForUser(User user) {
        return userDetailRepository.existsByUserId(user.getId());
    }

}
