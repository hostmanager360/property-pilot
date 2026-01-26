package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.dto.CreateAppartamentoDTO;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.model.Appartamento;
import com.propertypilot.coreservice.service.AppartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/private/appartamenti")
@RequiredArgsConstructor
public class AppartamentoController {

    @Autowired
    AppartamentoService appartamentoService;

    @PostMapping("/create")
    public ResponseEntity<ResponseHandler<AppartamentoDettagliDTO>> create(@RequestBody AppartamentoDettagliDTO dto) {

        try {
            AppartamentoDettagliDTO created = appartamentoService.createAppartamento(dto);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            created,
                            "Appartamento creato correttamente."
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore durante la creazione dell'appartamento")
            );
        }
    }
}
