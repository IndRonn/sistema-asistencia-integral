package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.login.LoginRequest;
import com.indra.asistencias.services.IAuthService;
import jakarta.servlet.http.HttpServletRequest; // <--- Importante
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders; // <--- Importante
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    // 1. ENDPOINT DE LOGIN (Público)
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // 2. ENDPOINT DE VALIDACIÓN (Privado - Requiere Token)
    // ESTE ES EL MÉTODO QUE PREGUNTAS
    @GetMapping("/check")
    public ResponseEntity<JwtResponse> checkToken(HttpServletRequest request) {

        // Extraemos el token del Header "Authorization"
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Validación defensiva (aunque el Filtro ya debería haberlo hecho)
        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = headerAuth.substring(7); // Quitamos "Bearer "

        // Llamamos al servicio para validar y obtener datos frescos
        JwtResponse response = authService.validateToken(token);

        return ResponseEntity.ok(response);
    }
}