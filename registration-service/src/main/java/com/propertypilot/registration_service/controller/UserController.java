package com.propertypilot.registration_service.controller;

import com.propertypilot.registration_service.exception.*;
import com.propertypilot.registration_service.model.ResponseHandler;
import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.UserDto;
import com.propertypilot.registration_service.service.UserService;
import com.propertypilot.registration_service.util.VerifyTokenRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private VerifyTokenRegistration verifyTokenRegistration;

    @PostMapping("/register")
    public ResponseEntity<ResponseHandler<User>> register(@RequestBody UserDto user) {
        try {
            userService.registerUser(user);

            return ResponseEntity.ok(
                    ResponseHandler.success(null, "Utente registrato. Conferma l'email per attivare l'account.")
            );

        } catch (PasswordMismatchException e) {
            return ResponseEntity.ok(ResponseHandler.error(1003, e.getMessage()));

        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.ok(ResponseHandler.error(1001, e.getMessage()));

        } catch (InvalidEmailException e) {
            return ResponseEntity.ok(ResponseHandler.error(1002, e.getMessage()));

        } catch (EmailSendException e) {
            return ResponseEntity.ok(ResponseHandler.error(2001, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.ok(ResponseHandler.error(9999, "Errore interno durante la registrazione"));
        }

    }

    @GetMapping("/verify")
    public ModelAndView verify(@RequestParam String token) {
        try {
            verifyTokenRegistration.verifyToken(token);
            return new ModelAndView("verify-success");
        } catch (TokenNotFoundException e) {
            return new ModelAndView("verify-error")
                    .addObject("message", "Token non valido");
        } catch (TokenExpiredException e) {
            return new ModelAndView("verify-error")
                    .addObject("message", "Token scaduto");
        } catch (Exception e) {
            return new ModelAndView("verify-error")
                    .addObject("message", "Errore interno durante la verifica");
        }
    }
}