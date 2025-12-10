package com.indra.asistencias.dto.asistencia;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class MarcaRespuestaDto implements Serializable {
    private String mensaje;          // "Entrada registrada con éxito"
    private String tipoMarca;        // "ENTRADA" | "SALIDA"
    private String horaExacta;       // "08:05:00"
    private String estadoAsistencia; // "P" (Puntual) | "T" (Tarde)
}