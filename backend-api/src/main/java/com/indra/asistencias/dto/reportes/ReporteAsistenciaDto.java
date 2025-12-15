package com.indra.asistencias.dto.reportes;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ReporteAsistenciaDto {
    private String username;
    private String nombreCompleto;
    private LocalDate fecha;
    private String horaEntrada;
    private String horaSalida;
    private String estado;
    private String estadoDescripcion;
    private String justificacion;
}