package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import java.util.Map; // <--- Import necesario
import java.util.Optional;

public interface AsistenciaRepositoryCustom {
    Optional<EstadoAsistenciaDto> obtenerEstadoActual(Long idUsuario);
    String registrarAsistencia(Long idUsuario, String ip, String device);
    Map<String, String> obtenerConfiguracionAsistencia();

    // NUEVO MÉTODO
    String solicitarJustificacion(Long idUsuario, Long idAsistencia, java.time.LocalDate fecha, String motivo, String tipo);
}