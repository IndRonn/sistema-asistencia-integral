package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.services.IAsistenciaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.indra.asistencias.models.AsistenciaView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDate;

@RestController
@RequestMapping("/asistencia") // CAMBIO: Singular (según contrato)
@RequiredArgsConstructor
public class AsistenciaController {

    private final IAsistenciaService asistenciaService;

    // Endpoint: GET /asistencia/estado-actual
    @GetMapping("/estado-actual")
    public ResponseEntity<EstadoAsistenciaDto> obtenerEstadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(asistenciaService.obtenerEstadoDashboard(username));
    }

    @PostMapping("/marcar")
    public ResponseEntity<MarcaRespuestaDto> registrarMarca(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Extraer metadatos para auditoría
        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent"); // "Mozilla/5.0..."

        MarcaRespuestaDto respuesta = asistenciaService.registrarMarcacion(auth.getName(), ip, device);

        return ResponseEntity.status(201).body(respuesta); // 201 Created
    }

    @GetMapping("/historial")
    public ResponseEntity<Page<AsistenciaView>> listarHistorial(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Page<AsistenciaView> historial = asistenciaService.obtenerHistorial(
                auth.getName(),
                fechaInicio,
                fechaFin,
                pageable
        );

        return ResponseEntity.ok(historial);
    }
}