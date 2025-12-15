package com.indra.asistencias.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitorVivoDto {
    private Long idUsuario;
    private String nombreCompleto;
    private String horaEntrada;
    private String ip;
    private String dispositivo;
}