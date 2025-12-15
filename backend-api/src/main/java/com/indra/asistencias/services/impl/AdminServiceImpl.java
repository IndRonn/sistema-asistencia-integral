package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.admin.JustificacionPendienteDto;
import com.indra.asistencias.dto.admin.ResolucionDto;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.AdminRepository;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.services.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements IAdminService {

    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JustificacionPendienteDto> obtenerPendientes() {
        log.info("Consultando bandeja de justificaciones pendientes...");
        return adminRepository.listarPendientes();
    }

    @Override
    public String resolver(Long idJustificacion, String adminUsername, ResolucionDto dto) {

        Usuario admin = usuarioRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado: " + adminUsername));

        log.info("Admin {} resolviendo justificación {} con estado {}", adminUsername, idJustificacion, dto.getEstado());

        return adminRepository.resolverJustificacion(
                idJustificacion,
                admin.getIdUsuario(),
                dto.getEstado(),
                dto.getComentario()
        );
    }
}