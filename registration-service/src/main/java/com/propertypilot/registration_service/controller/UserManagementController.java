package com.propertypilot.registration_service.controller;

import com.propertypilot.registration_service.model.ResponseHandler;
import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.UserDto;
import com.propertypilot.registration_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseHandler<User>> createAdmin(@RequestBody UserDto dto) {
        User user = userService.createAdmin(dto, dto.getTenantKey());
        return ResponseEntity.ok(ResponseHandler.success(user, "Admin creato"));
    }

    @PostMapping("/create-host")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ResponseHandler<User>> createHost(@RequestBody UserDto dto) {
        User user = userService.createHost(dto, dto.getTenantKey());
        return ResponseEntity.ok(ResponseHandler.success(user, "Host creato"));
    }

    @PostMapping("/create-cohost")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','HOST')")
    public ResponseEntity<ResponseHandler<User>> createCohost(@RequestBody UserDto dto) {
        User user = userService.createCohost(dto, dto.getTenantKey());
        return ResponseEntity.ok(ResponseHandler.success(user, "Cohost creato"));
    }
}
