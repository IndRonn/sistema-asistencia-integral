package com.indra.asistencias.services;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;

public interface IAsistenciaService {
    EstadoAsistenciaDto obtenerEstadoDashboard(String username);

    MarcaRespuestaDto registrarMarcacion(String username, String ip, String device);
}