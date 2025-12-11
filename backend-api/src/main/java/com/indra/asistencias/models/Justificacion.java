package com.indra.asistencias.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "JUSTIFICACION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Justificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_JUST_GEN")
    @SequenceGenerator(name = "SEQ_JUST_GEN", sequenceName = "SEQ_JUSTIFICACION", allocationSize = 1)
    @Column(name = "id_justificacion")
    private Long idJustificacion;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @Column(name = "fecha_justificar", nullable = false)
    private LocalDate fechaJustificar;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "estado", nullable = false)
    private String estado; // 'PENDIENTE', 'APROBADO', 'RECHAZADO'

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    // Auditoría automática antes de insertar
    @PrePersist
    public void prePersist() {
        this.fechaSolicitud = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
    }
}