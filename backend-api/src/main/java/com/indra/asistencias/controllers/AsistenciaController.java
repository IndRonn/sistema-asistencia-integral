package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.asistencia.AsistenciaHistorialDto;
import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.dto.justificacion.SolicitudJustificacionDto;
import com.indra.asistencias.services.IAsistenciaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/asistencia")
@RequiredArgsConstructor
public class AsistenciaController {

    private final IAsistenciaService asistenciaService;

    @GetMapping("/estado-actual")
    public ResponseEntity<EstadoAsistenciaDto> obtenerEstadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(asistenciaService.obtenerEstadoDashboard(auth.getName()));
    }

    @PostMapping("/marcar")
    public ResponseEntity<MarcaRespuestaDto> registrarMarca(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent");

        MarcaRespuestaDto respuesta = asistenciaService.registrarMarcacion(auth.getName(), ip, device);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/historial")
    public ResponseEntity<Page<AsistenciaHistorialDto>> listarHistorial(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Page<AsistenciaHistorialDto> historial = asistenciaService.obtenerHistorial(
                auth.getName(),
                fechaInicio,
                fechaFin,
                pageable
        );

        return ResponseEntity.ok(historial);
    }

    @PostMapping("/justificaciones")
    public ResponseEntity<Map<String, String>> crearSolicitud(@Valid @RequestBody SolicitudJustificacionDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        asistenciaService.solicitarJustificacion(auth.getName(), dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Solicitud enviada correctamente. Estado: PENDIENTE"));
    }
}