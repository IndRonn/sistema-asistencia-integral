package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.admin.JustificacionPendienteDto;
import java.util.List;

public interface AdminRepository {
    List<JustificacionPendienteDto> listarPendientes();
    String resolverJustificacion(Long idJustificacion, Long idAdmin, String estado, String comentario);
}