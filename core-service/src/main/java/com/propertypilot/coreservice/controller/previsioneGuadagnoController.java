package com.propertypilot.coreservice.controller;


import com.propertypilot.coreservice.context.TenantContext;
import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.exceptionCustom.PrenotazioneException;
import com.propertypilot.coreservice.model.Prenotazione;
import com.propertypilot.coreservice.model.Tenant;
import com.propertypilot.coreservice.service.PrevisioneGadagnoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/public")
public class previsioneGuadagnoController {

    @Autowired
    PrevisioneGadagnoService previsioneGadagnoService;

    @PostMapping("/calcoloPrevisioneGudagno")
    public ResponseEntity<ResponseHandler> calcoloPrevisioneGudagno(@RequestBody PrevisioneGuadagnoDto dto) {
        ResponseHandler responseHandler = new ResponseHandler();
        try {
            PrevisioneGuadagnoDto previsioneGuadagnoDto = previsioneGadagnoService.calcoloCostiPrevisioneGadagno(dto);
            return ResponseEntity.ok(
                    ResponseHandler.success(previsioneGuadagnoDto, "Lista prenotazioni recuperata correttamente")
            );
        } catch (PrenotazioneException e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(e.getCode(), e.getMessage())
            );
        }
    }
}
