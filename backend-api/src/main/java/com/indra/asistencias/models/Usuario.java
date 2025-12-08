package com.indra.asistencias.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USUARIO_GEN")
    @SequenceGenerator(name = "SEQ_USUARIO_GEN", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password; // Hash BCrypt

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol; // 'ADMIN' o 'EMPLEADO'

    @Column(name = "estado", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String estado; // 'A' (Activo) o 'I' (Inactivo)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Auditoría automática antes de insertar
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "A"; // Valor por defecto según Diccionario de Datos
        }
    }
}