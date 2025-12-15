package com.indra.asistencias.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadisticaSemanalDto {
    private String fecha;
    private String dia;
    private Integer total;
    private Integer puntuales;
    private Integer tardanzas;
    private Integer faltas;
}