package com.indra.asistencias.repositories.impl;

import com.indra.asistencias.dto.asistencia.DashboardDto;
import com.indra.asistencias.repositories.AsistenciaRepositoryCustom;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AsistenciaRepositoryImpl implements AsistenciaRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall procObtenerEstado;

    @PostConstruct
    public void init() {
        this.procObtenerEstado = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ASISTENCIA")
                .withProcedureName("SP_OBTENER_ESTADO_ACTUAL")
                .returningResultSet("p_cursor", (rs, rowNum) -> {

                    // 1. Extracción de Oracle
                    String estadoJornadaOracle = rs.getString("estado_actual_jornada"); // EN_CURSO, FINALIZADO
                    Timestamp tsEntrada = rs.getTimestamp("hora_entrada");
                    Timestamp tsSalida = rs.getTimestamp("hora_salida");
                    String estadoAsistencia = rs.getString("estado_asistencia"); // 'T', 'P', etc.

                    // 2. Formateo de Hora (ISO-8601 HH:mm:ss según contrato)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                    String horaEntradaStr = (tsEntrada != null) ? tsEntrada.toLocalDateTime().format(formatter) : null;
                    String horaSalidaStr = (tsSalida != null) ? tsSalida.toLocalDateTime().format(formatter) : null;

                    // 3. Traducción de Estado (Oracle -> Contrato API)
                    String estadoApi = "EN_CURSO".equals(estadoJornadaOracle) ? "EN_JORNADA" : "FINALIZADO";

                    // 4. Mensaje Dinámico
                    String mensaje = "EN_JORNADA".equals(estadoApi)
                            ? "Jornada en curso"
                            : "Jornada finalizada";

                    // 5. Detectar Tardanza
                    boolean esTardanza = "T".equals(estadoAsistencia);

                    return DashboardDto.builder()
                            .estado(estadoApi)
                            .mensaje(mensaje)
                            .horaEntrada(horaEntradaStr)
                            .horaSalida(horaSalidaStr)
                            .esTardanza(esTardanza)
                            .build();
                });
    }

    @Override
    public Optional<DashboardDto> obtenerEstadoActual(Long idUsuario) {
        try {
            Map<String, Object> results = procObtenerEstado.execute(Map.of("p_usuario_id", idUsuario));
            List<DashboardDto> list = (List<DashboardDto>) results.get("p_cursor");

            if (list != null && !list.isEmpty()) {
                return Optional.of(list.get(0));
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error llamando a SP_OBTENER_ESTADO_ACTUAL: {}", e.getMessage());
            return Optional.empty();
        }
    }
}