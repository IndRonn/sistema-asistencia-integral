package com.indra.asistencias.mappers;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.dto.usuario.UsuarioDto;
import com.indra.asistencias.models.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public JwtResponse toJwtResponse(Usuario usuario, String token) {

        // 1. Construir la Clase Interna (UsuarioDto)
        UsuarioDto usuarioDto = UsuarioDto.builder()
                .id(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombres() + " " + usuario.getApellidos()) // Concatenación aquí
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();

        // 2. Construir la Clase Externa (JwtResponse)
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .usuario(usuarioDto) // Anidamos el objeto
                .build();
    }
}