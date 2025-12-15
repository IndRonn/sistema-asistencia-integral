package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.login.LoginRequest;
import com.indra.asistencias.services.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    // Login
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // 2. ENDPOINT DE VALIDACIÓN
    @GetMapping("/check")
    public ResponseEntity<JwtResponse> checkToken(HttpServletRequest request) {

        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = headerAuth.substring(7);

        JwtResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}