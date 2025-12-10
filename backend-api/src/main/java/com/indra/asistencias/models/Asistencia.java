package com.indra.asistencias.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ASISTENCIA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ASISTENCIA_GEN")
    @SequenceGenerator(name = "SEQ_ASISTENCIA_GEN", sequenceName = "SEQ_ASISTENCIA", allocationSize = 1)
    @Column(name = "id_asistencia", nullable = false)
    private Long idAsistencia;

    // Relación Muchos-a-Uno: Muchas asistencias pertenecen a Un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha; // Solo la fecha (TRUNC)

    @Column(name = "hora_entrada", nullable = false)
    private LocalDateTime horaEntrada; // Timestamp completo

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida; // Puede ser NULL (Si está trabajando)

    // Valores: 'P' (Puntual), 'T' (Tarde), 'A' (Ausente), etc.
    // IMPORTANTE: Oracle CHAR(1) requiere columnDefinition fijo
    @Column(name = "estado_asistencia", nullable = false, columnDefinition = "CHAR(1)")
    private String estadoAsistencia;

    @Column(name = "ip_origen", length = 50)
    private String ipOrigen;

    @Column(name = "device_info", length = 100)
    private String deviceInfo;
}