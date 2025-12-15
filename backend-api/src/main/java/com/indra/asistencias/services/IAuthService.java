package com.indra.asistencias.services;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.login.LoginRequest;

public interface IAuthService {

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse validateToken(String token);
}