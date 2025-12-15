package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.asistencia.AsistenciaHistorialDto;
import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
public class AsistenciaRepository {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall procObtenerEstado;
    private SimpleJdbcCall procRegistrar;
    private SimpleJdbcCall procSolicitarJustificacion;

    @PostConstruct
    public void init() {

        this.procObtenerEstado = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ASISTENCIA")
                .withProcedureName("SP_OBTENER_ESTADO_ACTUAL")
                .returningResultSet("p_cursor", (rs, rowNum) -> {

                    // Extracción de Datos de Oracle
                    String estadoJornadaOracle = rs.getString("estado_actual_jornada");
                    String estadoAsistencia = rs.getString("estado_asistencia");
                    Timestamp tsEntrada = rs.getTimestamp("hora_entrada");
                    Timestamp tsSalida = rs.getTimestamp("hora_salida");

                    // Formateo de Horas
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String horaEntradaStr = (tsEntrada != null) ? tsEntrada.toLocalDateTime().format(formatter) : null;
                    String horaSalidaStr = (tsSalida != null) ? tsSalida.toLocalDateTime().format(formatter) : null;

                    // Lógica de Negocio
                    String estadoApi = "EN_CURSO".equals(estadoJornadaOracle) ? "EN_JORNADA" : "FINALIZADO";

                    String mensaje = "EN_JORNADA".equals(estadoApi)
                            ? "Jornada en curso"
                            : "Jornada finalizada";

                    boolean esTardanza = "T".equals(estadoAsistencia);

                    // Construcción del DTO
                    return EstadoAsistenciaDto.builder()
                            .estado(estadoApi)
                            .mensaje(mensaje)
                            .horaEntrada(horaEntradaStr)
                            .horaSalida(horaSalidaStr)
                            .esTardanza(esTardanza)
                            .build();
                });

        // REGISTRAR MARCA
        this.procRegistrar = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ASISTENCIA")
                .withProcedureName("SP_REGISTRAR_ASISTENCIA");

        this.procSolicitarJustificacion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ASISTENCIA")
                .withProcedureName("SP_SOLICITAR_JUSTIFICACION");
    }


    public String solicitarJustificacion(Long idUsuario, Long idAsistencia, java.time.LocalDate fecha, String motivo, String tipo) {
        try {
            Map<String, Object> inParams = Map.of(
                    "p_usuario_id", idUsuario,
                    "p_id_asistencia", idAsistencia != null ? idAsistencia : java.sql.Types.NULL,
                    "p_fecha", java.sql.Date.valueOf(fecha),
                    "p_motivo", motivo,
                    "p_tipo", tipo
            );

            Map<String, Object> out = procSolicitarJustificacion.execute(inParams);
            return (String) out.get("p_mensaje_out");

        } catch (Exception e) {
            log.error("Error en SP_SOLICITAR_JUSTIFICACION: {}", e.getMessage());
            throw e;
        }
    }


    public String registrarAsistencia(Long idUsuario, String ip, String device) {
        try {
            Map<String, Object> inParams = Map.of(
                    "p_usuario_id", idUsuario,
                    "p_ip_origen", ip != null ? ip : "UNKNOWN",
                    "p_device", device != null ? device : "UNKNOWN"
            );

            Map<String, Object> out = procRegistrar.execute(inParams);
            return (String) out.get("p_mensaje");

        } catch (Exception e) {
            log.error("Error crítico en SP_REGISTRAR_ASISTENCIA: {}", e.getMessage());
            throw e;
        }
    }


    public Optional<EstadoAsistenciaDto> obtenerEstadoActual(Long idUsuario) {
        try {
            Map<String, Object> results = procObtenerEstado.execute(Map.of("p_usuario_id", idUsuario));
            List<EstadoAsistenciaDto> list = (List<EstadoAsistenciaDto>) results.get("p_cursor");

            if (list != null && !list.isEmpty()) {
                EstadoAsistenciaDto dto = list.get(0);
                injectarConfiguracion(dto);
                return Optional.of(dto);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error llamando a SP_OBTENER_ESTADO_ACTUAL: {}", e.getMessage());
            return Optional.empty();
        }
    }


    public Map<String, String> obtenerConfiguracionAsistencia() {
        try {
            String sql = "SELECT clave, valor FROM CONFIGURACION WHERE clave IN ('HORA_ENTRADA', 'TOLERANCIA_MINUTOS')";
            return jdbcTemplate.query(sql, (rs) -> {
                Map<String, String> config = new java.util.HashMap<>();
                while (rs.next()) {
                    config.put(rs.getString("clave"), rs.getString("valor"));
                }
                return config;
            });
        } catch (Exception ex) {
            log.error("Error leyendo configuración: {}", ex.getMessage());
            return Map.of("HORA_ENTRADA", "08:00", "TOLERANCIA_MINUTOS", "15");
        }
    }

    private void injectarConfiguracion(EstadoAsistenciaDto dto) {
        Map<String, String> config = obtenerConfiguracionAsistencia();
        dto.setHoraInicioConfig(config.getOrDefault("HORA_ENTRADA", "08:00"));
        dto.setToleranciaMinutos(config.getOrDefault("TOLERANCIA_MINUTOS", "15"));
    }


    public Page<AsistenciaHistorialDto> listarHistorial(Long idUsuario, Pageable pageable) {

        // 1. Count
        String countSql = "SELECT COUNT(*) FROM ASISTENCIA WHERE id_usuario = ?";
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, idUsuario);
        if (total == null) total = 0L;

        // 2. Query Principal (CON TRADUCCIÓN CASE WHEN)
        String sql = """
            SELECT 
                a.id_asistencia, 
                a.fecha, 
                to_char(a.hora_entrada, 'HH24:MI:SS') as hora_entrada, 
                to_char(a.hora_salida, 'HH24:MI:SS') as hora_salida, 
                a.estado_asistencia,
                
                -- TRADUCCIÓN DE ESTADO (Soluciona el estadoDescripcion NULL)
                CASE a.estado_asistencia
                    WHEN 'P' THEN 'PUNTUAL'
                    WHEN 'T' THEN 'TARDANZA'
                    WHEN 'A' THEN 'FALTA'
                    WHEN 'J' THEN 'JUSTIFICADO'
                    ELSE 'DESCONOCIDO'
                END as estado_descripcion,

                j.estado as estado_just,
                j.admin_comentarios as mensaje_admin 
            FROM ASISTENCIA a
            LEFT JOIN JUSTIFICACION j ON a.id_asistencia = j.id_asistencia
            WHERE a.id_usuario = ?
            ORDER BY a.fecha DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;

        List<AsistenciaHistorialDto> lista = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return AsistenciaHistorialDto.builder()
                    .idAsistencia(rs.getLong("id_asistencia"))
                    .fecha(rs.getDate("fecha").toLocalDate())
                    .horaEntrada(rs.getString("hora_entrada"))
                    .horaSalida(rs.getString("hora_salida"))
                    .estado(rs.getString("estado_asistencia"))

                    // AQUI MAPEAMOS EL NUEVO CAMPO
                    .estadoDescripcion(rs.getString("estado_descripcion"))

                    .justificacionEstado(rs.getString("estado_just"))
                    .mensajeAdmin(rs.getString("mensaje_admin"))
                    .build();
        }, idUsuario, pageable.getOffset(), pageable.getPageSize());

        return new PageImpl<>(lista, pageable, total);
    }
}