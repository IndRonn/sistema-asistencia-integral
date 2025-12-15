package com.indra.asistencias.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "CONFIGURACION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion implements Serializable {

    @Id
    @Column(name = "clave", length = 50)
    private String clave;

    @Column(name = "valor", nullable = false)
    private String valor;

    @Column(name = "descripcion")
    private String descripcion;
}