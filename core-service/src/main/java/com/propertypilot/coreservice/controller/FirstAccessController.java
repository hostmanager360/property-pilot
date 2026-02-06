package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.dto.FirstAccessStatusResponse;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.dto.UserDetailDto;
import com.propertypilot.coreservice.exceptionCustom.InvalidTenantDataException;
import com.propertypilot.coreservice.exceptionCustom.StatusTenantNotFoundException;
import com.propertypilot.coreservice.exceptionCustom.TenantAlreadyExistsException;
import com.propertypilot.coreservice.exceptionCustom.TipoLicenzaNotFoundException;
import com.propertypilot.coreservice.service.FirstAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        try {
            firstAccessService.createTenant(dto);
            return ResponseEntity.ok(ResponseHandler.success(null, "Tenant creato correttamente"));

        } catch (TenantAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHandler.error(3001, e.getMessage()));

        } catch (TipoLicenzaNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseHandler.error(3002, e.getMessage()));

        } catch (StatusTenantNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseHandler.error(3003, e.getMessage()));

        } catch (InvalidTenantDataException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseHandler.error(3004, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHandler.error(9999, "Errore interno durante la creazione del tenant"));
        }

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


