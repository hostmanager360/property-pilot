package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.exceptionCustom.ApartmentAlreadyAssignedException;
import com.propertypilot.coreservice.exceptionCustom.ApartmentNotFoundException;
import com.propertypilot.coreservice.exceptionCustom.UserNotFoundException;
import com.propertypilot.coreservice.model.UserApartmentAccess;
import com.propertypilot.coreservice.service.UserApartmentAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/private/userApartment")
public class UserApartmentAccessController {

    private final UserApartmentAccessService service;

    public UserApartmentAccessController(UserApartmentAccessService service) {
        this.service = service;
    }

    @PostMapping("/assign")
    public ResponseEntity<ResponseHandler<UserApartmentAccess>> assign(
            @RequestParam Long userId,
            @RequestParam Integer apartmentId
    ) {
        try {
            UserApartmentAccess access = service.assignApartmentToUser(userId, apartmentId);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            access,
                            "Appartamento assegnato correttamente all'utente."
                    )
            );

        } catch (UserNotFoundException e) {
            return ResponseEntity.ok(ResponseHandler.error(4001, e.getMessage()));

        } catch (ApartmentNotFoundException e) {
            return ResponseEntity.ok(ResponseHandler.error(4002, e.getMessage()));

        } catch (ApartmentAlreadyAssignedException e) {
            return ResponseEntity.ok(ResponseHandler.error(4003, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore interno durante l'assegnazione dell'appartamento.")
            );
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseHandler<String>> remove(
            @RequestParam Long userId,
            @RequestParam Integer apartmentId
    ) {
        try {
            service.removeApartmentFromUser(userId, apartmentId);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            "OK",
                            "Appartamento rimosso correttamente dall'utente."
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore interno durante la rimozione dell'appartamento.")
            );
        }
    }

    @GetMapping("/getUserApartmentAccess")
    public ResponseEntity<ResponseHandler<List<UserApartmentAccess>>> getUserApartments(@RequestParam Long userId) {
        try {
            List<UserApartmentAccess> list = service.getApartmentsForUser(userId);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            list,
                            "Lista appartamenti ottenuta correttamente."
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore interno durante il recupero degli appartamenti.")
            );
        }
    }
    @DeleteMapping("/removeAllByUser")
    public ResponseEntity<ResponseHandler<String>> removeAll(@RequestParam Long userId) {
        try {
            service.removeAllForUser(userId);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            "OK",
                            "Tutti gli appartamenti sono stati rimossi dall'utente."
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore interno durante la rimozione degli appartamenti.")
            );
        }
    }
    @GetMapping("/getAppartamentiByUser")
    public ResponseEntity<ResponseHandler<List<AppartamentoDettagliDTO>>> getApartmentIds(@RequestParam Long userId) {
        try {
            List<AppartamentoDettagliDTO> result = service.getAppartamentiDettagliByUser(userId);

            return ResponseEntity.ok(
                    ResponseHandler.success(
                            result,
                            "Lista appartamenti e dettagli ottenuta correttamente."
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseHandler.error(9999, "Errore interno durante il recupero degli appartamenti.")
            );
        }
    }
}
