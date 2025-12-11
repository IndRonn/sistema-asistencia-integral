package com.indra.asistencias.repositories;

import com.indra.asistencias.models.Justificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface JustificacionRepository extends JpaRepository<Justificacion, Long> {

    // Validación: ¿Ya existe una solicitud PENDIENTE o APROBADA para este usuario y fecha?
    // Si ya fue RECHAZADA, le permitimos intentar de nuevo.
    boolean existsByIdUsuarioAndFechaJustificarAndEstadoIn(Long idUsuario, LocalDate fecha, java.util.List<String> estados);
}