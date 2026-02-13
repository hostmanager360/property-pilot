package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.dto.PrevisioneGuadagnoListDto;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.service.PrevisioneGadagnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/private/previsione-guadagno")
@RequiredArgsConstructor
@Slf4j
public class PrevisioneGuadagnoController {

    private final PrevisioneGadagnoService previsioneGadagnoService;

    @PostMapping("/calcoloPrevisione")
    public ResponseEntity<ResponseHandler<?>> calcoloPrevisioneGuadagno(@RequestBody PrevisioneGuadagnoDto dto) {

        log.info("Richiesta calcolo previsione guadagno");

        PrevisioneGuadagno previsione = previsioneGadagnoService.calcoloCostiPrevisioneGadagno(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(previsione, "Previsione salvata correttamente")
        );
    }
    @GetMapping("/getAllPrevisioni")
    public ResponseEntity<ResponseHandler<?>> getAll() {

        log.info("Richiesta lista previsioni guadagno");

        List<PrevisioneGuadagnoListDto> lista = previsioneGadagnoService.getAllPrevisioni();

        return ResponseEntity.ok(
                ResponseHandler.success(lista, "Lista previsioni recuperata correttamente")
        );
    }

    @GetMapping("/getPrevisioneById")
    public ResponseEntity<ResponseHandler<?>> getDettaglio(@RequestParam Long id) {
        log.info("Richiesta dettaglio previsione guadagno id={}", id);

        PrevisioneGuadagnoDto dto = previsioneGadagnoService.getDettaglioById(id);

        return ResponseEntity.ok(
                ResponseHandler.success(dto, "Dettaglio previsione recuperato correttamente")
        );
    }

    @DeleteMapping("/deletePrevisioneById")
    public ResponseEntity<ResponseHandler<?>> delete(@RequestParam Long id) {
        log.info("Richiesta delete previsione id={}", id);

        previsioneGadagnoService.deleteById(id);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Previsione eliminata correttamente")
        );
    }
}