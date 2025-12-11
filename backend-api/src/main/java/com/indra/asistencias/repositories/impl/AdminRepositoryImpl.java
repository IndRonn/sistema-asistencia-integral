package com.indra.asistencias.repositories.impl;

import com.indra.asistencias.dto.admin.JustificacionPendienteDto;
import com.indra.asistencias.repositories.AdminRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AdminRepositoryImpl implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall procResolver;

    @PostConstruct
    public void init() {
        this.procResolver = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ADMIN")
                .withProcedureName("SP_RESOLVER_JUSTIFICACION");
    }

    @Override
    public List<JustificacionPendienteDto> listarPendientes() {
        // Consulta directa a la VISTA optimizada del DBA
        String sql = "SELECT * FROM V_JUSTIFICACIONES_PENDIENTES ORDER BY fecha_solicitud ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("fecha_solicitud");

            return JustificacionPendienteDto.builder()
                    .idJustificacion(rs.getLong("id_justificacion"))
                    .empleadoNombre(rs.getString("empleado_nombre"))
                    .rol(rs.getString("rol"))
                    .fechaJustificar(rs.getDate("fecha_justificar").toLocalDate())
                    .motivo(rs.getString("motivo"))
                    .tipo(rs.getString("tipo"))
                    .fechaSolicitud(ts != null ? ts.toLocalDateTime() : null)
                    .build();
        });
    }

    @Override
    public String resolverJustificacion(Long idJustificacion, Long idAdmin, String estado, String comentario) {
        try {
            Map<String, Object> inParams = Map.of(
                    "p_id_justificacion", idJustificacion,
                    "p_nuevo_estado", estado,
                    "p_admin_id", idAdmin,
                    "p_comentario", comentario != null ? comentario : "Sin comentarios"
            );

            Map<String, Object> out = procResolver.execute(inParams);
            return (String) out.get("p_mensaje");

        } catch (Exception e) {
            log.error("Error en SP_RESOLVER_JUSTIFICACION: {}", e.getMessage());
            throw e; // El GlobalHandler capturará los errores -20003 y -20004
        }
    }
}