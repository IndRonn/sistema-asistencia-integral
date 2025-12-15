package com.indra.asistencias.dto.asistencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaHistorialDto {
    private Long idAsistencia;
    private LocalDate fecha;
    private String horaEntrada;
    private String horaSalida;
    private String estado;
    private String estadoDescripcion;
    private String justificacionEstado;
    private String mensajeAdmin;
}