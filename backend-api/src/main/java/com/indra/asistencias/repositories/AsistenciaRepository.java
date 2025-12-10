package com.indra.asistencias.repositories;

import com.indra.asistencias.models.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaRepository extends
        JpaRepository<Asistencia, Long>, // 1. Hereda el poder de JPA (save, findById)
        AsistenciaRepositoryCustom {    // 2. Hereda el poder del SP (obtenerEstadoActual)

    // Aquí puedes poner métodos Query Methods normales si los necesitas
    // Optional<Asistencia> findTopByUsuario... etc.
}