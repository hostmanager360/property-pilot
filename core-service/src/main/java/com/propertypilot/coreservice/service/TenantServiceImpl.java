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
    public Tenant createTenant(User user, CreateTenantDTO dto) {

        if (user.getTenantKey() != null) {
            throw new TenantAlreadyExistsException("L'utente ha già un tenant associato.");
        }
        validateTenantData(dto);
        String keyTenant = generateKeyTenant();

        TipoLicenza tipoLicenza = tipoLicenzaRepository.findById(dto.getTipoLicenzaId())
                .orElseThrow(() -> new TipoLicenzaNotFoundException("Tipo licenza non trovato"));

        StatusTenant status = statusTenantRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new StatusTenantNotFoundException("Status tenant non trovato"));

        if (dto.getDataScadenza() != null && dto.getDataScadenza().isBefore(dto.getDataIscrizione())) {
            throw new InvalidTenantDataException("La data di scadenza non può essere precedente alla data di iscrizione");
        }

        Tenant tenant = Tenant.builder()
                .keyTenant(keyTenant)
                .tipoSoggetto(dto.getTipoSoggetto())
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .codiceFiscale(dto.getCodiceFiscale())
                .ragioneSociale(dto.getRagioneSociale())
                .partitaIva(dto.getPartitaIva())
                .viaResidenza(dto.getViaResidenza())
                .civicoResidenza(dto.getCivicoResidenza())
                .cittaResidenza(dto.getCittaResidenza())
                .capResidenza(dto.getCapResidenza())
                .viaSedeFisica(dto.getViaSedeFisica())
                .cittaSedeFisica(dto.getCittaSedeFisica())
                .capSedeFisica(dto.getCapSedeFisica())
                .dataIscrizione(dto.getDataIscrizione())
                .dataScadenza(dto.getDataScadenza())
                .tipoLicenza(tipoLicenza)
                .status(status)
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        user.setTenantKey(keyTenant);
        userRepository.save(user);

        return savedTenant;
    }

    private void validateTenantData(CreateTenantDTO dto) {

        if (!dto.getTipoSoggetto().equals("PERSONA_FISICA") &&
                !dto.getTipoSoggetto().equals("AZIENDA")) {
            throw new InvalidTenantDataException("Tipo soggetto non valido");
        }

        // Persona fisica
        if (dto.getTipoSoggetto().equals("PERSONA_FISICA")) {

            if (isBlank(dto.getNome()) || isBlank(dto.getCognome())) {
                throw new InvalidTenantDataException("Nome e cognome sono obbligatori per persona fisica");
            }

            if (isBlank(dto.getCodiceFiscale())) {
                throw new InvalidTenantDataException("Codice fiscale obbligatorio per persona fisica");
            }

            if (isBlank(dto.getViaResidenza()) ||
                    isBlank(dto.getCivicoResidenza()) ||
                    isBlank(dto.getCittaResidenza()) ||
                    isBlank(dto.getCapResidenza())) {
                throw new InvalidTenantDataException("Indirizzo di residenza obbligatorio per persona fisica");
            }
        }

        // Azienda
        if (dto.getTipoSoggetto().equals("AZIENDA")) {

            if (!isBlank(dto.getCodiceFiscale())) {
                throw new InvalidTenantDataException("Il codice fiscale non deve essere inserito per un'azienda");
            }

            if (isBlank(dto.getRagioneSociale())) {
                throw new InvalidTenantDataException("La ragione sociale è obbligatoria per un'azienda");
            }

            if (!isBlank(dto.getViaResidenza()) ||
                    !isBlank(dto.getCivicoResidenza()) ||
                    !isBlank(dto.getCittaResidenza()) ||
                    !isBlank(dto.getCapResidenza())) {
                throw new InvalidTenantDataException("La residenza non deve essere inserita per un'azienda");
            }
        }

        // Sede fisica (sempre obbligatoria)
        if (isBlank(dto.getViaSedeFisica()) ||
                isBlank(dto.getCittaSedeFisica()) ||
                isBlank(dto.getCapSedeFisica())) {
            throw new InvalidTenantDataException("La sede fisica è obbligatoria");
        }

        // Date
        if (dto.getDataScadenza() != null &&
                dto.getDataScadenza().isBefore(dto.getDataIscrizione())) {
            throw new InvalidTenantDataException("La data di scadenza non può essere precedente alla data di iscrizione");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
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