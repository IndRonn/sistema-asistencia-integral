package com.indra.asistencias.services;

import com.indra.asistencias.dto.reportes.ReporteAsistenciaDto;
import java.time.LocalDate;
import java.util.List;

public interface IReporteService {

    List<ReporteAsistenciaDto> obtenerReporte(LocalDate inicio, LocalDate fin, Long idEmpleado);
}
