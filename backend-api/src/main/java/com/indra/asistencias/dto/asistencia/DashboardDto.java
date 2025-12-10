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
public class DashboardDto implements Serializable {

    // Nombres EXACTOS del contrato
    private String estado;        // Valores: "SIN_MARCAR" | "EN_JORNADA" | "FINALIZADO"
    private String mensaje;       // Feedback al usuario
    private String horaEntrada;   // Formato "HH:mm:ss"
    private String horaSalida;    // Formato "HH:mm:ss" (Puede ser null)
    private Boolean esTardanza;   // Para pintar alertas en Front
}