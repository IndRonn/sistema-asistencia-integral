package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.usuario.CambioEstadoDto;
import com.indra.asistencias.dto.usuario.UsuarioRequestDto;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> listarUsuarios(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return usuarioRepository.findAll(pageable);
        }
        return usuarioRepository.buscarPorTermino(search, pageable);
    }

    @Override
    @Transactional
    public Usuario crearUsuario(UsuarioRequestDto dto) {
        // 1. Validar Duplicados
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El username ya existe"); // GlobalHandler lo debe mapear a 409
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }

        // 2. Crear Entidad
        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .rol(dto.getRol())
                .estado("A") //Por defecto
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public String cambiarEstado(Long id, CambioEstadoDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado(dto.getEstado());
        usuarioRepository.save(usuario);

        return dto.getEstado();
    }
}