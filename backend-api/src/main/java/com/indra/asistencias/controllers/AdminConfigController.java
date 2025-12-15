package com.indra.asistencias.controllers;

import com.indra.asistencias.services.IConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/configuracion")
@RequiredArgsConstructor
public class AdminConfigController {


    private final IConfiguracionService configService;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(configService.listarConfiguracion());
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody List<Map<String, String>> cambios) {
        configService.actualizarConfiguracion(cambios);
        return ResponseEntity.ok(Map.of("mensaje", "Configuración actualizada"));
    }
}