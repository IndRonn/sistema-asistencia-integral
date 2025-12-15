package com.indra.asistencias.services;

import com.indra.asistencias.dto.asistencia.AsistenciaHistorialDto; // <--- Importante: El nuevo DTO
import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.dto.justificacion.SolicitudJustificacionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IAsistenciaService {

    // Obtener estado actual usando Username
    EstadoAsistenciaDto obtenerEstadoDashboard(String username);

    // Registrar entrada/salida y devolver respuesta compleja
    MarcaRespuestaDto registrarMarcacion(String username, String ip, String device);

    // Listar asistencias (con mensaje Admin) paginadas
    Page<AsistenciaHistorialDto> obtenerHistorial(String username, LocalDate desde, LocalDate hasta, Pageable pageable);

    // Justificar: Crear solicitud
    void solicitarJustificacion(String username, SolicitudJustificacionDto dto);
}