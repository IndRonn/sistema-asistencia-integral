package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import java.util.Optional;

public interface AsistenciaRepositoryCustom {
    // Método que llamará al SP
    Optional<EstadoAsistenciaDto> obtenerEstadoActual(Long idUsuario);

    String registrarAsistencia(Long idUsuario, String ip, String device);
}