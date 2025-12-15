package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.admin.JustificacionPendienteDto;
import com.indra.asistencias.dto.admin.ResolucionDto;
import com.indra.asistencias.services.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    @GetMapping("/justificaciones")
    public ResponseEntity<List<JustificacionPendienteDto>> listarPendientes() {
        return ResponseEntity.ok(adminService.obtenerPendientes());
    }

    @PutMapping("/justificaciones/{id}/resolucion")
    public ResponseEntity<Map<String, String>> resolver(
            @PathVariable Long id,
            @Valid @RequestBody ResolucionDto dto
    ) {
        // Obtener admin que aprueba justificación
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Llamar al servicio
        String mensaje = adminService.resolver(id, adminUsername, dto);

        // Respuesta
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}