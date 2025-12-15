package com.indra.asistencias.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiResumenDto {
    private Integer totalEmpleados;
    private Integer presentes;
    private Integer ausentes;
    private Integer puntuales;
    private Integer tardanzas;
    private Double tasaAsistencia;
}