package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.asistencia.DashboardDto;
import java.util.Optional;

public interface AsistenciaRepositoryCustom {
    // Método que llamará al SP
    Optional<DashboardDto> obtenerEstadoActual(Long idUsuario);
}