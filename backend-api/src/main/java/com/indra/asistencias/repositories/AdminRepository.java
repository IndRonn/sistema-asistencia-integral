package com.indra.asistencias.repositories;

import com.indra.asistencias.dto.admin.JustificacionPendienteDto;
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
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall procResolver;

    @PostConstruct
    public void init() {

        // Llamamos a los objetos PL/SQL
        this.procResolver = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_ADMIN")
                .withProcedureName("SP_GESTIONAR_JUSTIFICACION");
    }

    public List<JustificacionPendienteDto> listarPendientes() {
        // Query para la vista
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

    public String resolverJustificacion(Long idJustificacion, Long idAdmin, String estado, String comentario) {
        try {
            Map<String, Object> inParams = Map.of(
                    "p_id_justificacion", idJustificacion,
                    "p_nuevo_estado", estado,
                    "p_id_admin", idAdmin, // CORRECCIÓN 2: Coincidir con 'p_id_admin' del DBA
                    "p_comentario", comentario != null ? comentario : "Sin comentarios"
            );

            // Ejecutamos el SP
            Map<String, Object> out = procResolver.execute(inParams);


            String mensaje = (String) out.get("O_MENSAJE");

            if (mensaje == null) {
                mensaje = (String) out.get("o_mensaje");
            }

            return mensaje != null ? mensaje : "Operación exitosa (Sin mensaje de BD)";
        } catch (Exception e) {
            log.error("Error crítico llamando a PKG_ADMIN.SP_GESTIONAR_JUSTIFICACION: {}", e.getMessage());
            throw e;
        }
    }
}