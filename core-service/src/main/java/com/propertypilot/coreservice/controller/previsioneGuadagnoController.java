package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.service.PrevisioneGadagnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/public/previsione-guadagno")
@RequiredArgsConstructor
@Slf4j
public class PrevisioneGuadagnoController {

    private final PrevisioneGadagnoService previsioneGadagnoService;

    @PostMapping
    public ResponseEntity<ResponseHandler<?>> calcoloPrevisioneGuadagno(@RequestBody PrevisioneGuadagnoDto dto) {

        log.info("Richiesta calcolo previsione guadagno");

        PrevisioneGuadagno previsione = previsioneGadagnoService.calcoloCostiPrevisioneGadagno(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(previsione, "Previsione salvata correttamente")
        );
    }
}