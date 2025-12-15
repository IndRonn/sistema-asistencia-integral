package com.indra.asistencias.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResolucionDto {
    @NotNull(message = "El estado es obligatorio")
    @Pattern(regexp = "^(APROBADO|RECHAZADO)$", message = "Solo se permite APROBADO o RECHAZADO")
    private String estado;

    private String comentario;
}