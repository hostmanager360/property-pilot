package com.propertypilot.coreservice.util;

import com.propertypilot.coreservice.dto.TenantResponseDTO;
import com.propertypilot.coreservice.model.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantResponseDTO toDTO(Tenant t) {
        if (t == null) return null;

        return TenantResponseDTO.builder()
                .keyTenant(t.getKeyTenant())
                .tipoSoggetto(t.getTipoSoggetto())
                .nome(t.getNome())
                .cognome(t.getCognome())
                .codiceFiscale(t.getCodiceFiscale())
                .ragioneSociale(t.getRagioneSociale())
                .partitaIva(t.getPartitaIva())
                .viaResidenza(t.getViaResidenza())
                .civicoResidenza(t.getCivicoResidenza())
                .cittaResidenza(t.getCittaResidenza())
                .capResidenza(t.getCapResidenza())
                .viaSedeFisica(t.getViaSedeFisica())
                .cittaSedeFisica(t.getCittaSedeFisica())
                .capSedeFisica(t.getCapSedeFisica())
                .dataIscrizione(t.getDataIscrizione())
                .dataScadenza(t.getDataScadenza())
                .tipoLicenzaId(t.getTipoLicenza().getId())
                .statusId(t.getStatus().getId())
                .build();
    }
}