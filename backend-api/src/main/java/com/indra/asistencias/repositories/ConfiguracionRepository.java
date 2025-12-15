package com.indra.asistencias.repositories;

import com.indra.asistencias.models.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {

    Optional<Configuracion> findByClave(String clave);
}