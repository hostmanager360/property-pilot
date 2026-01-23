package com.propertypilot.coreservice.util;

import com.propertypilot.coreservice.dto.TenantResponseDTO;
import com.propertypilot.coreservice.model.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {
    public TenantResponseDTO toDTO(Tenant tenant) {
        return TenantResponseDTO.builder()
                .keyTenant(tenant.getKeyTenant())
                .nome(tenant.getNome())
                .cognome(tenant.getCognome())
                .codiceFiscale(tenant.getCodiceFiscale())
                .partitaIva(tenant.getPartitaIva())
                .viaResidenza(tenant.getViaResidenza())
                .civicoResidenza(tenant.getCivicoResidenza())
                .cittaResidenza(tenant.getCittaResidenza())
                .capResidenza(tenant.getCapResidenza())
                .dataIscrizione(tenant.getDataIscrizione())
                .dataScadenza(tenant.getDataScadenza())
                .tipoLicenza(tenant.getTipoLicenza().getCodiceLicenza())
                .status(tenant.getStatus().getStatusCode())
                .build();
    }

}
