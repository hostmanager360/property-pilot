package com.propertypilot.service;

import com.propertypilot.model.StatusTenant;
import com.propertypilot.repository.StatusTenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusTenantServiceImpl implements StatusTenantService {

    private final StatusTenantRepository repository;

    public StatusTenantServiceImpl(StatusTenantRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StatusTenant> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<StatusTenant> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<StatusTenant> findByStatusCode(String statusCode) {
        return repository.findByStatusCode(statusCode);
    }

    @Override
    public StatusTenant save(StatusTenant statusTenant) {
        return repository.save(statusTenant);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
