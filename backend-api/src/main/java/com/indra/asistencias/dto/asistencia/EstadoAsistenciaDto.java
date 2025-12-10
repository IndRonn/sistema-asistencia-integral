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

    // ⚠️ CAMBIO CRÍTICO: De 'Boolean' a 'boolean'
    // Al ser primitivo, si no se le asigna valor, por defecto será 'false'. NUNCA 'null'.
    private boolean esTardanza;
}