package com.indra.asistencias.repositories;

import com.indra.asistencias.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Optional<Usuario> findByUsername(String username);


    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Filtra por username, nombres o apellidos que contengan el texto
    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Usuario> buscarPorTermino(@Param("search") String search, Pageable pageable);
}
