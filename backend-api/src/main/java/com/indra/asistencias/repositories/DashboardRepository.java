package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.dashboard.EstadisticaSemanalDto;
import com.indra.asistencias.dto.dashboard.KpiResumenDto;
import com.indra.asistencias.dto.dashboard.MonitorVivoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    // Vista 1: Quién está trabajando AHORA
    public List<MonitorVivoDto> obtenerEnVivo() {
        String sql = "SELECT * FROM V_MONITOR_VIVO ORDER BY hora_entrada DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> MonitorVivoDto.builder()
                .idUsuario(rs.getLong("id_usuario"))
                .nombreCompleto(rs.getString("nombre_completo"))
                .horaEntrada(rs.getString("hora_entrada"))
                .ip(rs.getString("ip_origen"))
                .dispositivo(rs.getString("device_info"))
                .build()
        );
    }

    // Vista 2: Estadísticas del día (Optimizada)
    public KpiResumenDto obtenerKpisDia() {
        String sql = "SELECT * FROM V_RESUMEN_DIA";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            int total = rs.getInt("total_empleados");
            int presentes = rs.getInt("presentes");

            double tasa = (total > 0) ? ((double) presentes / total) * 100 : 0.0;

            return KpiResumenDto.builder()
                    .totalEmpleados(total)
                    .presentes(presentes)
                    .puntuales(rs.getInt("puntuales"))
                    .tardanzas(rs.getInt("tardanzas"))
                    .ausentes(rs.getInt("ausentes")) // Dato directo de la Vista del DBA
                    .tasaAsistencia(Math.round(tasa * 100.0) / 100.0) // Redondeo a 2 decimales
                    .build();
        });
    }

    public List<EstadisticaSemanalDto> obtenerEstadisticasSemanales() {
        String sql = "SELECT * FROM V_ESTADISTICAS_SEMANALES";

        return jdbcTemplate.query(sql, (rs, rowNum) -> EstadisticaSemanalDto.builder()

                .fecha(rs.getString("fecha"))
                .dia(rs.getString("dia"))
                .total(rs.getInt("total"))
                .puntuales(rs.getInt("puntuales"))
                .tardanzas(rs.getInt("tardanzas"))
                .faltas(rs.getInt("faltas"))
                .build()
        );
    }
}