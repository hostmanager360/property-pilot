package com.propertypilot.service;

import com.propertypilot.model.Tenant;
import com.propertypilot.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repository;

    public TenantServiceImpl(TenantRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tenant> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Tenant> findByKey(String keyTenant) {
        return repository.findById(keyTenant);
    }

    @Override
    public Tenant save(Tenant tenant) {
        return repository.save(tenant);
    }

    @Override
    public void deleteByKey(String keyTenant) {
        repository.deleteById(keyTenant);
    }
}