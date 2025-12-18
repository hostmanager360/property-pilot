package com.propertypilot.controller;

import com.propertypilot.dto.ResponseHandler;
import com.propertypilot.exceptionCustom.PrenotazioneException;
import com.propertypilot.model.Prenotazione;
import com.propertypilot.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/prenotazioniController")
public class PrenotazioniController {

    @Autowired
    PrenotazioneService prenotazioneService;

    @GetMapping
    public ResponseEntity<ResponseHandler> getAllReservation(@RequestParam String keyTenant) {
        ResponseHandler responseHandler = new ResponseHandler();
        try {
            List<Prenotazione> prenotazioni = prenotazioneService.findAllByTenant(keyTenant);
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
