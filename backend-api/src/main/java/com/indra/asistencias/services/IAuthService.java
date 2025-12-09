package com.indra.asistencias.services;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.login.LoginRequest;

public interface IAuthService {
    // Método existente
    JwtResponse login(LoginRequest loginRequest);

    // NUEVO MÉTODO (Agrega esto)
    JwtResponse validateToken(String token);
}