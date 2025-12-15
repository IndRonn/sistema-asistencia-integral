package com.indra.asistencias.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "V_ASISTENCIA_DETALLADA")
@Immutable
@Data
@NoArgsConstructor
public class AsistenciaView implements Serializable {

    @Id
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "email")
    private String email;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_entrada_str")
    private String horaEntrada;

    @Column(name = "hora_salida_str")
    private String horaSalida;

    @Column(name = "estado_asistencia", columnDefinition = "CHAR(1)")
    private String estado;

    @Column(name = "estado_texto")
    private String estadoTexto;

    @Column(name = "es_tardanza")
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean esTardanza;

    @Column(name = "es_justificable")
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean esJustificable;
}