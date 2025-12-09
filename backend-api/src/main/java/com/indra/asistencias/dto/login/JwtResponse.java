package com.indra.asistencias.dto.login;

import com.indra.asistencias.dto.usuario.UsuarioDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;

    @Builder.Default // Asegura que el builder use este valor por defecto
    private String type = "Bearer";

    private UsuarioDto usuario; // Objeto anidado
}