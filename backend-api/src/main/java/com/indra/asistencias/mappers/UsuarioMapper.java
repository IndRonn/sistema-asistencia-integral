package com.indra.asistencias.mappers;

import com.indra.asistencias.dto.login.JwtResponse;
import com.indra.asistencias.models.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public JwtResponse toJwtResponse(Usuario usuario, String token){
        return JwtResponse.builder()
                .token(token)
                .tipo("Bearer")
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .build();
    }
}
