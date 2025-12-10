package com.indra.asistencias.services;

import com.indra.asistencias.dto.asistencia.DashboardDto;

public interface IAsistenciaService {
    DashboardDto obtenerEstadoDashboard(String username);
}