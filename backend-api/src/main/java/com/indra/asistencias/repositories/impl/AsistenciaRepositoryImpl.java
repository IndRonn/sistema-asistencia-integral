package com.indra.asistencias.repositories.impl;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
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
    private SimpleJdbcCall procRegistrar;

    @PostConstruct
    public void init() {
        // 1. Configuración de Lectura (OBTENER ESTADO)
        this.procObtenerEstado = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ASISTENCIA")
                .withProcedureName("SP_OBTENER_ESTADO_ACTUAL")
                .returningResultSet("p_cursor", (rs, rowNum) -> {

                    // --- A. Extracción de Datos de Oracle ---
                    String estadoJornadaOracle = rs.getString("estado_actual_jornada"); // 'EN_CURSO' | 'FINALIZADO'
                    String estadoAsistencia = rs.getString("estado_asistencia");        // 'P', 'T', etc.
                    Timestamp tsEntrada = rs.getTimestamp("hora_entrada");
                    Timestamp tsSalida = rs.getTimestamp("hora_salida");

                    // --- B. Formateo de Horas (HH:mm:ss) ---
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String horaEntradaStr = (tsEntrada != null) ? tsEntrada.toLocalDateTime().format(formatter) : null;
                    String horaSalidaStr = (tsSalida != null) ? tsSalida.toLocalDateTime().format(formatter) : null;

                    // --- C. Lógica de Negocio ---
                    // Traducción de estado
                    String estadoApi = "EN_CURSO".equals(estadoJornadaOracle) ? "EN_JORNADA" : "FINALIZADO";

                    // Mensaje amigable
                    String mensaje = "EN_JORNADA".equals(estadoApi)
                            ? "Jornada en curso"
                            : "Jornada finalizada";

                    // Cálculo seguro de Tardanza (Evita NullPointer)
                    // Si la columna es NULL, "T".equals(null) devuelve false. Es seguro.
                    boolean esTardanza = "T".equals(estadoAsistencia);

                    // --- D. Construcción del DTO ---
                    return EstadoAsistenciaDto.builder()
                            .estado(estadoApi)
                            .mensaje(mensaje)
                            .horaEntrada(horaEntradaStr)
                            .horaSalida(horaSalidaStr)
                            .esTardanza(esTardanza) // Asignación crítica
                            .build();
                });

        // 2. Configuración de Escritura (REGISTRAR MARCA)
        this.procRegistrar = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ASISTENCIA")
                .withProcedureName("SP_REGISTRAR_ASISTENCIA");
    }

    @Override
    public String registrarAsistencia(Long idUsuario, String ip, String device) {
        try {
            // Mapeo automático de parámetros IN
            Map<String, Object> inParams = Map.of(
                    "p_usuario_id", idUsuario,
                    "p_ip_origen", ip != null ? ip : "UNKNOWN",
                    "p_device", device != null ? device : "UNKNOWN"
            );

            // Ejecución del SP
            Map<String, Object> out = procRegistrar.execute(inParams);

            // Retorno del mensaje de salida (OUT)
            return (String) out.get("p_mensaje");

        } catch (Exception e) {
            log.error("Error crítico en SP_REGISTRAR_ASISTENCIA: {}", e.getMessage());
            throw e; // Relanzamos para que Spring maneje la excepción (GlobalExceptionHandler)
        }
    }

    @Override
    public Optional<EstadoAsistenciaDto> obtenerEstadoActual(Long idUsuario) {
        try {
            // 1. Ejecutar el SP Principal
            Map<String, Object> results = procObtenerEstado.execute(Map.of("p_usuario_id", idUsuario));
            List<EstadoAsistenciaDto> list = (List<EstadoAsistenciaDto>) results.get("p_cursor");

            if (list != null && !list.isEmpty()) {
                EstadoAsistenciaDto dto = list.get(0);

                // 2. ENRIQUECIMIENTO TÁCTICO: Leer Configuración
                // (Hacemos esto aquí para no modificar el SP complejo por ahora)
                injectarConfiguracion(dto);

                return Optional.of(dto);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error llamando a SP_OBTENER_ESTADO_ACTUAL: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> obtenerConfiguracionAsistencia() {
        try {
            // Consultamos la tabla CONFIGURACION
            String sql = "SELECT clave, valor FROM CONFIGURACION WHERE clave IN ('HORA_ENTRADA', 'TOLERANCIA_MINUTOS')";

            // Usamos un extractor para convertir ResultSet a Map
            return jdbcTemplate.query(sql, (rs) -> {
                Map<String, String> config = new java.util.HashMap<>();
                while (rs.next()) {
                    config.put(rs.getString("clave"), rs.getString("valor"));
                }
                return config;
            });
        } catch (Exception ex) {
            log.error("Error leyendo configuración: {}", ex.getMessage());
            // Solo aquí usamos hardcode como ÚLTIMO recurso si la BD explota
            return Map.of("HORA_ENTRADA", "08:00", "TOLERANCIA_MINUTOS", "15");
        }
    }

    // Modificamos este método privado para que use el método público de arriba
    private void injectarConfiguracion(EstadoAsistenciaDto dto) {
        Map<String, String> config = obtenerConfiguracionAsistencia();

        // Asignación segura con valores por defecto si el mapa viniera vacío
        dto.setHoraInicioConfig(config.getOrDefault("HORA_ENTRADA", "08:00"));
        dto.setToleranciaMinutos(config.getOrDefault("TOLERANCIA_MINUTOS", "15"));
    }
}