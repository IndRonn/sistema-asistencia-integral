package com.indra.asistencias.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "V_ASISTENCIA_DETALLADA") // Mapeo directo a la Vista SQL
@Immutable // Optimización de Hibernate (Solo lectura, no Dirty Checking)
@Data
@NoArgsConstructor
public class AsistenciaView implements Serializable {

    @Id // Aunque es una vista, JPA necesita un ID para funcionar
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

    @Column(name = "hora_entrada_str") // Ya viene como "08:00:00" desde Oracle
    private String horaEntrada;

    @Column(name = "hora_salida_str")
    private String horaSalida;

    @Column(name = "estado_asistencia", columnDefinition = "CHAR(1)") // <--- AGREGAR ESTO
    private String estado;

    @Column(name = "estado_texto") // 'PUNTUAL', 'TARDANZA'
    private String estadoTexto;

    @Column(name = "es_tardanza")
    @Convert(converter = NumericBooleanConverter.class) // <--- LA MAGIA
    private Boolean esTardanza;

    @Column(name = "es_justificable")
    @Convert(converter = NumericBooleanConverter.class) // Convierte 1/0 a true/false
    private Boolean esJustificable;
}