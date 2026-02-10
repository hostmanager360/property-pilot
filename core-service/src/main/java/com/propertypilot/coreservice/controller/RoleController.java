package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.ResponseHandler;
import com.propertypilot.coreservice.model.Role;
import com.propertypilot.coreservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/roles")
public class RoleController {

    @Autowired
    RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/getRoles")
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ResponseHandler.success(roles, "Ruoli caricati con successo"));
    }
}
