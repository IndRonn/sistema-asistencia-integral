package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.login.LoginRequest;
import com.indra.asistencias.mappers.UsuarioMapper;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.security.JwtUtils;
import com.indra.asistencias.services.IAuthService; // <--- Import correjido
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService { // <--- Implementación de la Interfaz con 'I'

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public JwtResponse login(LoginRequest request) {
        // 1. Autenticar (Lanza excepción si falla)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 2. Establecer contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generar Token
        String jwt = jwtUtils.generateToken(request.getUsername());

        // 4. Obtener datos de usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        log.info("Usuario logueado exitosamente: {}", usuario.getUsername());

        // 5. Mapear respuesta
        return usuarioMapper.toJwtResponse(usuario, jwt);
    }

    @Override
    public JwtResponse validateToken(String token) {
        // 1. Validar integridad criptográfica (Firma y Expiración)
        if (!jwtUtils.validateJwtToken(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        // 2. Extraer usuario del token
        String username = jwtUtils.getUsernameFromToken(token);

        // 3. Buscar datos frescos en la BD (Por si lo bloquearon hace 1 segundo)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 4. Retornar la respuesta
        return usuarioMapper.toJwtResponse(usuario, token);
    }
}