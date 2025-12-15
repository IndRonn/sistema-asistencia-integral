package com.indra.asistencias.services;

import com.indra.asistencias.dto.usuario.CambioEstadoDto;
import com.indra.asistencias.dto.usuario.UsuarioRequestDto;
import com.indra.asistencias.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUsuarioService {

    Page<Usuario> listarUsuarios(String search, Pageable pageable) ;


    Usuario crearUsuario(UsuarioRequestDto dto);

    String cambiarEstado(Long id, CambioEstadoDto dto);
}
