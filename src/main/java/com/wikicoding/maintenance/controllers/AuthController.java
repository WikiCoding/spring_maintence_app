package com.wikicoding.maintenance.controllers;

import com.wikicoding.maintenance.dtos.LoginRequest;
import com.wikicoding.maintenance.dtos.LoginResponse;
import com.wikicoding.maintenance.dtos.UserRegisterRequest;
import com.wikicoding.maintenance.persistence.datamodel.User;
import com.wikicoding.maintenance.services.AuthenticationService;
import com.wikicoding.maintenance.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserRegisterRequest request) {
        if (request.getUsername().trim().isEmpty() ||
                request.getPassword().trim().isEmpty() ||
                request.getRole().trim().isEmpty()
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters.");
        }

        User user = authenticationService.register(request.getUsername(), request.getPassword(), request.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        if (request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters.");
        }

        User user = authenticationService.authenticate(request.getUsername(), request.getPassword());

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(user.getUsername(), token));
    }
}
