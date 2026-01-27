package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.dto.FirstAccessStatusResponse;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.service.FirstAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/private/first-access")
@RequiredArgsConstructor
public class FirstAccessController {

    private final FirstAccessService firstAccessService;

    @GetMapping("/status")
    public ResponseEntity<ResponseHandler<FirstAccessStatusResponse>> getStatus() {
        return ResponseEntity.ok(
                ResponseHandler.success(firstAccessService.getStatus(), "Stato primo accesso recuperato")
        );
    }

    @PostMapping("/create-tenant")
    public ResponseEntity<ResponseHandler<Void>> createTenant(@RequestBody CreateTenantDTO dto) {
        firstAccessService.createTenant(dto);
        return ResponseEntity.ok(ResponseHandler.success(null, "Tenant creato correttamente"));
    }

    @PostMapping("/user-detail")
    public ResponseEntity<ResponseHandler<Void>> completeUserDetail(@RequestBody UserDetailDto dto) {
        firstAccessService.completeUserDetail(dto);
        return ResponseEntity.ok(ResponseHandler.success(null, "Dettagli utente completati"));
    }

    @PostMapping("/complete")
    public ResponseEntity<ResponseHandler<Void>> completeFirstAccess() {
        firstAccessService.completeFirstAccess();
        return ResponseEntity.ok(ResponseHandler.success(null, "Primo accesso completato"));
    }
}


