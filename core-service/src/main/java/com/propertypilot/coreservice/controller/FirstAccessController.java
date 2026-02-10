package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.dto.FirstAccessStatusResponse;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.service.FirstAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/private/first-access")
@RequiredArgsConstructor
@Slf4j
public class FirstAccessController {

    private final FirstAccessService firstAccessService;

    @GetMapping("/status")
    public ResponseEntity<ResponseHandler<FirstAccessStatusResponse>> getStatus() {

        log.info("Richiesta stato primo accesso");

        FirstAccessStatusResponse status = firstAccessService.getStatus();

        return ResponseEntity.ok(
                ResponseHandler.success(status, "Stato primo accesso recuperato")
        );
    }

    @PostMapping("/create-tenant")
    public ResponseEntity<ResponseHandler<Void>> createTenant(@RequestBody CreateTenantDTO dto) {

        log.info("Richiesta creazione tenant: {}", dto.getNome());

        firstAccessService.createTenant(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Tenant creato correttamente")
        );
    }

    @PostMapping("/user-detail")
    public ResponseEntity<ResponseHandler<Void>> completeUserDetail(@RequestBody UserDetailDto dto) {

        log.info("Completamento dettagli utente");

        firstAccessService.completeUserDetail(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Dettagli utente completati")
        );
    }

    @PostMapping("/complete")
    public ResponseEntity<ResponseHandler<Void>> completeFirstAccess() {

        log.info("Completamento primo accesso");

        firstAccessService.completeFirstAccess();

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Primo accesso completato")
        );
    }
}