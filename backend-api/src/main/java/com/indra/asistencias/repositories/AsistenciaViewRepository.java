package com.indra.asistencias.repositories;

import com.indra.asistencias.models.AsistenciaView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AsistenciaViewRepository extends JpaRepository<AsistenciaView, Long> {

    // Query Method mágico de Spring Data
    // "Buscar por ID de usuario Y rango de fechas"
    Page<AsistenciaView> findByIdUsuarioAndFechaBetweenOrderByFechaDesc(
            Long idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable
    );
}