package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.login.LoginRequest;
import com.indra.asistencias.mappers.UsuarioMapper;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.security.JwtUtils;
import com.indra.asistencias.services.IAuthService;
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
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public JwtResponse login(LoginRequest request) {
        // 1. Autenticar (Lanza excepción si falla - BadCredentialsException)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 2. Establecer contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generar Token
        // CORRECCIÓN TÁCTICA: Pasamos el objeto 'authentication' completo, no el string.
        // Esto permite que JwtUtils extraiga el ROL internamente.
        String jwt = jwtUtils.generateToken(authentication);

        // 4. Obtener datos de usuario (Para devolver el perfil en el JSON)
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
        // CORRECCIÓN TÁCTICA: Usamos el nombre actualizado del método
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // 3. Buscar datos frescos en la BD
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 4. Retornar la respuesta (Token renovado o el mismo)
        return usuarioMapper.toJwtResponse(usuario, token);
    }
}