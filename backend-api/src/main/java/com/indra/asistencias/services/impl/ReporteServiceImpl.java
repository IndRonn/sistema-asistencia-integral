package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.reportes.ReporteAsistenciaDto;
import com.indra.asistencias.repositories.ReporteRepository;
import com.indra.asistencias.services.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReporteService {

    private final ReporteRepository reporteRepository;

    public List<ReporteAsistenciaDto> obtenerReporte(LocalDate inicio, LocalDate fin, Long idEmpleado) {

        if (inicio == null) inicio = LocalDate.now().withDayOfMonth(1);
        if (fin == null) fin = LocalDate.now();

        return reporteRepository.generarReporte(inicio, fin, idEmpleado);
    }

}