package com.indra.asistencias.dto.asistencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoAsistenciaDto implements Serializable {
    private String estado;
    private String mensaje;
    private String horaEntrada;
    private String horaSalida;
    private boolean esTardanza;
    private String horaInicioConfig;
    private String toleranciaMinutos;
}