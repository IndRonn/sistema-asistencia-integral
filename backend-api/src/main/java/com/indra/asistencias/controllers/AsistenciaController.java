package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.services.IAsistenciaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}