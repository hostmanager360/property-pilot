package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.exceptionCustom.InvalidTenantDataException;
import com.propertypilot.coreservice.exceptionCustom.StatusTenantNotFoundException;
import com.propertypilot.coreservice.exceptionCustom.TenantAlreadyExistsException;
import com.propertypilot.coreservice.exceptionCustom.TipoLicenzaNotFoundException;
import com.propertypilot.coreservice.model.StatusTenant;
import com.propertypilot.coreservice.model.Tenant;
import com.propertypilot.coreservice.model.TipoLicenza;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.StatusTenantRepository;
import com.propertypilot.coreservice.repository.TenantRepository;
import com.propertypilot.coreservice.repository.TipoLicenzaRepository;
import com.propertypilot.coreservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    TenantRepository tenantRepository;
    @Autowired
    TipoLicenzaRepository tipoLicenzaRepository;
    @Autowired
    StatusTenantRepository statusTenantRepository;
    @Autowired
    UserRepository userRepository;

    public TenantServiceImpl(TenantRepository repository) {
        this.tenantRepository = repository;
    }

    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @Override
    public Optional<Tenant> findByKey(String keyTenant) {
        return tenantRepository.findById(keyTenant);
    }

    @Override
    public Tenant save(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteByKey(String keyTenant) {
        tenantRepository.deleteById(keyTenant);
    }

    @Transactional
    @Override
    public Tenant createTenant(CreateTenantDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User non trovato"));

        if (user.getTenantKey() != null) {
            throw new TenantAlreadyExistsException("L'utente ha già un tenant associato.");
        }

        String keyTenant = generateKeyTenant();

        TipoLicenza tipoLicenza = tipoLicenzaRepository.findById(dto.getTipoLicenzaId())
                .orElseThrow(() -> new TipoLicenzaNotFoundException("Tipo licenza non trovato"));

        StatusTenant status = statusTenantRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new StatusTenantNotFoundException("Status tenant non trovato"));

        // eventuali validazioni aggiuntive
        if (dto.getDataScadenza() != null && dto.getDataScadenza().isBefore(dto.getDataIscrizione())) {
            throw new InvalidTenantDataException("La data di scadenza non può essere precedente alla data di iscrizione");
        }

        Tenant tenant = Tenant.builder()
                .keyTenant(keyTenant)
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .codiceFiscale(dto.getCodiceFiscale())
                .partitaIva(dto.getPartitaIva())
                .viaResidenza(dto.getViaResidenza())
                .civicoResidenza(dto.getCivicoResidenza())
                .cittaResidenza(dto.getCittaResidenza())
                .capResidenza(dto.getCapResidenza())
                .dataIscrizione(dto.getDataIscrizione())
                .dataScadenza(dto.getDataScadenza())
                .tipoLicenza(tipoLicenza)
                .status(status)
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        // 5. Aggiorna l’utente con la keyTenant
        user.setTenantKey(keyTenant);
        userRepository.save(user);

        return savedTenant;

    }
    private String generateKeyTenant() {
        String lastKey = tenantRepository.findLastKeyTenant(); // es: "TENANT023"

        if (lastKey == null) {
            return "TENANT001";
        }

        int number = Integer.parseInt(lastKey.replace("TENANT", ""));
        number++;

        return String.format("TENANT%03d", number);
    }

}