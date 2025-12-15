package com.indra.asistencias.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsuarioRequestDto {
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    @NotBlank
    private String nombres;
    @NotBlank
    private String apellidos;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^(ADMIN|EMPLEADO)$", message = "Rol inválido")
    private String rol;
}