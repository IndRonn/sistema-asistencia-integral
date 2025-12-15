package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.asistencia.AsistenciaHistorialDto;
import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto;
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.dto.justificacion.SolicitudJustificacionDto;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.AsistenciaRepository;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.services.IAsistenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaServiceImpl implements IAsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
    @Transactional(readOnly = true)
    public EstadoAsistenciaDto obtenerEstadoDashboard(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return asistenciaRepository.obtenerEstadoActual(usuario.getIdUsuario())
                .orElseGet(() -> {
                    Map<String, String> config = asistenciaRepository.obtenerConfiguracionAsistencia();

                    return EstadoAsistenciaDto.builder()
                            .estado("SIN_MARCAR")
                            .mensaje("Listo para iniciar jornada.")
                            .horaEntrada(null)
                            .horaSalida(null)
                            .esTardanza(false)
                            .horaInicioConfig(config.getOrDefault("HORA_ENTRADA", "08:00"))
                            .toleranciaMinutos(config.getOrDefault("TOLERANCIA_MINUTOS", "15"))
                            .build();
                });
    }

    @Override
    @Transactional
    public MarcaRespuestaDto registrarMarcacion(String username, String ip, String device) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String mensajeOracle = asistenciaRepository.registrarAsistencia(usuario.getIdUsuario(), ip, device);

        EstadoAsistenciaDto estado = asistenciaRepository.obtenerEstadoActual(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Inconsistencia: Se registró asistencia pero no se recuperó estado."));

        boolean esEntrada = "EN_JORNADA".equals(estado.getEstado());
        String estadoLetra = estado.isEsTardanza() ? "T" : "P";

        return MarcaRespuestaDto.builder()
                .mensaje(mensajeOracle)
                .tipoMarca(esEntrada ? "ENTRADA" : "SALIDA")
                .horaExacta(esEntrada ? estado.getHoraEntrada() : estado.getHoraSalida())
                .estadoAsistencia(estadoLetra)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaHistorialDto> obtenerHistorial(String username, LocalDate desde, LocalDate hasta, Pageable pageable) {
        // 1. Obtener Usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return asistenciaRepository.listarHistorial(usuario.getIdUsuario(), pageable);
    }

    @Override
    @Transactional
    public void solicitarJustificacion(String username, SolicitudJustificacionDto dto) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        asistenciaRepository.solicitarJustificacion(
                usuario.getIdUsuario(),
                dto.getIdAsistencia(),
                dto.getFecha(),
                dto.getMotivo(),
                dto.getTipo()
        );

        log.info("Solicitud creada para usuario: {}", username);
    }
}