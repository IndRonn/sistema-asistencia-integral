package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.reportes.ReporteAsistenciaDto;
import com.indra.asistencias.services.IExportService;
import com.indra.asistencias.services.impl.ReporteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteServiceImpl reporteService;
    private final IExportService exportService;

    // Reporte en pantalla
    @GetMapping
    public ResponseEntity<List<ReporteAsistenciaDto>> generar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) Long idEmpleado
    ) {
        return ResponseEntity.ok(reporteService.obtenerReporte(inicio, fin, idEmpleado));
    }

    // 2. Descargar Reporte
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarArchivo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) Long idEmpleado,
            @RequestParam(defaultValue = "EXCEL") String formato
    ) {
        try {
            // 1. Obtener Datos
            List<ReporteAsistenciaDto> datos = reporteService.obtenerReporte(inicio, fin, idEmpleado);

            byte[] archivo;
            String filename;
            MediaType mediaType;

            // 2. Escogeer formato
            if ("PDF".equalsIgnoreCase(formato)) {
                archivo = exportService.generarPdfReporte(datos);
                filename = "reporte.pdf";
                mediaType = MediaType.APPLICATION_PDF;
            } else {
                archivo = exportService.generarExcelReporte(datos);
                filename = "reporte.xlsx";
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(archivo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}