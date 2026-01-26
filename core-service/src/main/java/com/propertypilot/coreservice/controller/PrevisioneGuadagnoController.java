package com.propertypilot.coreservice.controller;


import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.exceptionCustom.PrevisioneGuadagnoException;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.service.PrevisioneGadagnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/public")
public class PrevisioneGuadagnoController {

    @Autowired
    PrevisioneGadagnoService previsioneGadagnoService;

    @PostMapping("/calcoloPrevisioneGudagno")
    public ResponseEntity<ResponseHandler> calcoloPrevisioneGudagno(@RequestBody PrevisioneGuadagnoDto dto) {
        ResponseHandler responseHandler = new ResponseHandler();
        try {
            PrevisioneGuadagno previsioneGuadagno = previsioneGadagnoService.calcoloCostiPrevisioneGadagno(dto);
            return ResponseEntity.ok(
                    ResponseHandler.success(previsioneGuadagno, "Previsione salvata correttamente")
            );
        } catch (PrevisioneGuadagnoException e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(e.getCode(), e.getMessage())
            );
        }
    }
}
