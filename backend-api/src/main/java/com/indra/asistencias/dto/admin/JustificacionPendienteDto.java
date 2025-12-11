package com.indra.asistencias.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class JustificacionPendienteDto {
    private Long idJustificacion;
    private String empleadoNombre;
    private String rol;
    private LocalDate fechaJustificar;
    private String motivo;
    private String tipo;
    private LocalDateTime fechaSolicitud;
}