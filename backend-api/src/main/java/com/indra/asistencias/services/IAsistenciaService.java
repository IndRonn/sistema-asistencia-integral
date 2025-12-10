package com.indra.asistencias.services;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.models.AsistenciaView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IAsistenciaService {
    EstadoAsistenciaDto obtenerEstadoDashboard(String username);

    MarcaRespuestaDto registrarMarcacion(String username, String ip, String device);

    Page<AsistenciaView> obtenerHistorial(String username, LocalDate desde, LocalDate hasta, Pageable pageable);
}