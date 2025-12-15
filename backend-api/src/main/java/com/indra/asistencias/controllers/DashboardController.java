package com.indra.asistencias.controllers;

import com.indra.asistencias.services.impl.DashboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServiceImpl dashboardService;

    @GetMapping("/live")
    public ResponseEntity<?> monitorEnVivo() {
        return ResponseEntity.ok(dashboardService.getMonitorEnVivo());
    }

    @GetMapping("/kpis")
    public ResponseEntity<?> resumenEstadistico() {
        return ResponseEntity.ok(dashboardService.getKpis());
    }

    @GetMapping("/semanal")
    public ResponseEntity<?> estadisticasSemanales() {
        return ResponseEntity.ok(dashboardService.getEstadisticasSemanales());
    }
}