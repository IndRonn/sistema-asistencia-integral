package com.indra.asistencias.services;

import com.indra.asistencias.dto.admin.JustificacionPendienteDto;
import com.indra.asistencias.dto.admin.ResolucionDto;

import java.util.List;

public interface IAdminService {

    List<JustificacionPendienteDto> obtenerPendientes();

    // Emitir sentencia (Aprobar/Rechazar)
    String resolver(Long idJustificacion, String adminUsername, ResolucionDto dto);
}