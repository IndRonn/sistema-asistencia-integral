package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.asistencia.DashboardDto;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.AsistenciaRepository;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.services.IAsistenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaServiceImpl implements IAsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDto obtenerEstadoDashboard(String username) {

        // 1. Obtener ID del usuario (El username viene del Token)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. Consultar a Oracle vía nuestro Repositorio Híbrido
        return asistenciaRepository.obtenerEstadoActual(usuario.getIdUsuario())
                .orElseGet(() -> {
                    // ESCENARIO 1 DEL CONTRATO: Nuevo día (Sin marcas)
                    return DashboardDto.builder()
                            .estado("SIN_MARCAR") // Valor exacto del contrato
                            .mensaje("Listo para iniciar")
                            .horaEntrada(null)
                            .horaSalida(null)
                            .esTardanza(false)
                            .build();
                });
    }
}