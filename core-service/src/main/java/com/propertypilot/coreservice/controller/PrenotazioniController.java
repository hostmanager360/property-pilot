package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.context.TenantContext;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.exceptionCustom.PrenotazioneException;
import com.propertypilot.coreservice.model.Prenotazione;
import com.propertypilot.coreservice.model.Tenant;
import com.propertypilot.coreservice.service.PrenotazioneService;
import com.propertypilot.coreservice.service.TenantService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/core/private")
public class PrenotazioniController {

    @Autowired
    PrenotazioneService prenotazioneService;
    @Autowired
    TenantService tenantService;

    @GetMapping("/getAllReservation")
    public ResponseEntity<ResponseHandler> getAllReservation() {
        String keyTenant = TenantContext.getTenant();
        if (keyTenant == null) {
            throw new IllegalStateException("Tenant non presente nel contesto di sicurezza");
        }
        ResponseHandler responseHandler = new ResponseHandler();
        try {
            Tenant tenant = tenantService.findByKey(keyTenant)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Tenant non trovato per key: " + keyTenant));

            List<Prenotazione> prenotazioni = prenotazioneService.findAllByTenant(tenant);
            return ResponseEntity.ok(
                    ResponseHandler.success(prenotazioni, "Lista prenotazioni recuperata correttamente")
            );
        } catch (PrenotazioneException e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(e.getCode(), e.getMessage())
            );
        }
    }
}
