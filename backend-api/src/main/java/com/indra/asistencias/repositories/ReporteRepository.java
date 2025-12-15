package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.reportes.ReporteAsistenciaDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReporteRepository {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall procGenerarReporte;

    @PostConstruct
    public void init() {

        this.procGenerarReporte = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_REPORTES")
                .withProcedureName("SP_GENERAR_REPORTE_ASISTENCIA")
                .returningResultSet("p_cursor", (rs, rowNum) -> {

                    return ReporteAsistenciaDto.builder()
                            .username(rs.getString("username"))
                            .nombreCompleto(rs.getString("nombre_completo"))
                            .fecha(rs.getDate("fecha").toLocalDate())
                            .horaEntrada(rs.getString("hora_entrada"))
                            .horaSalida(rs.getString("hora_salida"))
                            .estado(rs.getString("estado_asistencia"))
                            .estadoDescripcion(rs.getString("estado_desc"))
                            .justificacion(rs.getString("justificacion_motivo"))
                            .build();
                });
    }

    public List<ReporteAsistenciaDto> generarReporte(LocalDate inicio, LocalDate fin, Long idUsuario) {
        try {

            Map<String, Object> inParams = new java.util.HashMap<>();
            inParams.put("p_fecha_inicio", java.sql.Date.valueOf(inicio));
            inParams.put("p_fecha_fin", java.sql.Date.valueOf(fin));
            inParams.put("p_usuario_id", idUsuario);

            log.info("Ejecutando Reporte con params: Inicio={}, Fin={}, Usuario={}", inicio, fin, idUsuario);

            Map<String, Object> result = procGenerarReporte.execute(inParams);

            return (List<ReporteAsistenciaDto>) result.get("p_cursor");

        } catch (Exception e) {
            log.error("Error generando reporte: {}", e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}