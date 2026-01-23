package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.CreateTenantDTO;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.dto.TenantResponseDTO;
import com.propertypilot.coreservice.exceptionCustom.InvalidTenantDataException;
import com.propertypilot.coreservice.exceptionCustom.StatusTenantNotFoundException;
import com.propertypilot.coreservice.exceptionCustom.TenantAlreadyExistsException;
import com.propertypilot.coreservice.exceptionCustom.TipoLicenzaNotFoundException;
import com.propertypilot.coreservice.model.Tenant;
import com.propertypilot.coreservice.service.TenantService;
import com.propertypilot.coreservice.util.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/private/tenant")
public class TenantController {
    @Autowired
    TenantService tenantService;
    @Autowired
    TenantMapper tenantMapper;

    @PostMapping("/createTenant")
    public ResponseEntity<ResponseHandler<TenantResponseDTO>> createTenant(@RequestBody CreateTenantDTO dto) {

        try {
            Tenant created = tenantService.createTenant(dto);
            TenantResponseDTO response = tenantMapper.toDTO(created);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            response,
                            "Tenant creato correttamente."
                    )
            );

        } catch (TenantAlreadyExistsException e) {
            return ResponseEntity.ok(ResponseHandler.error(3001, e.getMessage()));

        } catch (TipoLicenzaNotFoundException e) {
            return ResponseEntity.ok(ResponseHandler.error(3002, e.getMessage()));

        } catch (StatusTenantNotFoundException e) {
            return ResponseEntity.ok(ResponseHandler.error(3003, e.getMessage()));

        } catch (InvalidTenantDataException e) {
            return ResponseEntity.ok(ResponseHandler.error(3004, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore interno durante la creazione del tenant")
            );
        }
    }

}

