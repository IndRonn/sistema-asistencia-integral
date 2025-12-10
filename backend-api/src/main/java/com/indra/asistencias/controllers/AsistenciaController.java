package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.asistencia.DashboardDto;
import com.indra.asistencias.services.IAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asistencia") // CAMBIO: Singular (según contrato)
@RequiredArgsConstructor
public class AsistenciaController {

    private final IAsistenciaService asistenciaService;

    // Endpoint: GET /asistencia/estado-actual
    @GetMapping("/estado-actual")
    public ResponseEntity<DashboardDto> obtenerEstadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(asistenciaService.obtenerEstadoDashboard(username));
    }
}