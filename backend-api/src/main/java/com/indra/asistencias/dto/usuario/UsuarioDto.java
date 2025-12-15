package com.indra.asistencias.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private String rol;
}