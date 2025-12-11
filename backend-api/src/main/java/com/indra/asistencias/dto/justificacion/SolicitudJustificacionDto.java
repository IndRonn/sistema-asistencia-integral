package com.indra.asistencias.dto.justificacion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class SolicitudJustificacionDto implements Serializable {

    private Long idAsistencia; // Puede ser NULL (si es falta total)

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;   // La fecha que se quiere justificar

    @NotNull
    @Size(min = 10, max = 255, message = "El motivo debe tener entre 10 y 255 caracteres")
    private String motivo;

    @NotNull
    private String tipo;       // 'SALUD', 'PERSONAL', 'TRABAJO'
}