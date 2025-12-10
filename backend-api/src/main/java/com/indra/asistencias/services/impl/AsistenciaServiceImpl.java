package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto; // <--- Usamos el nombre correcto
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
import com.indra.asistencias.models.AsistenciaView;
import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.AsistenciaRepository;
import com.indra.asistencias.repositories.AsistenciaViewRepository;
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
    private final AsistenciaViewRepository asistenciaViewRepository;

    @Override
    @Transactional(readOnly = true)
    public EstadoAsistenciaDto obtenerEstadoDashboard(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return asistenciaRepository.obtenerEstadoActual(usuario.getIdUsuario())
                .orElseGet(() -> {
                    // 1. Obtener configuración fresca desde la BD
                    Map<String, String> config = asistenciaRepository.obtenerConfiguracionAsistencia();

                    // 2. Construir DTO con datos reales
                    return EstadoAsistenciaDto.builder()
                            .estado("SIN_MARCAR")
                            .mensaje("Listo para iniciar jornada.")
                            .horaEntrada(null)
                            .horaSalida(null)
                            .esTardanza(false)

                            // SIN HARDCODE: Usamos lo que diga Oracle
                            .horaInicioConfig(config.getOrDefault("HORA_ENTRADA", "08:00"))
                            .toleranciaMinutos(config.getOrDefault("TOLERANCIA_MINUTOS", "15"))
                            .build();
                });
    }

    @Override
    @Transactional
    public MarcaRespuestaDto registrarMarcacion(String username, String ip, String device) {
        // 1. Identificar Usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. COMMAND: Ejecutar el SP (Oracle escribe)
        String mensajeOracle = asistenciaRepository.registrarAsistencia(usuario.getIdUsuario(), ip, device);

        // 3. QUERY: Leer el estado "fresco"
        EstadoAsistenciaDto estado = asistenciaRepository.obtenerEstadoActual(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Inconsistencia: Se registró asistencia pero no se recuperó estado."));

        // 4. Lógica de Respuesta
        boolean esEntrada = "EN_JORNADA".equals(estado.getEstado());

        // FIX DEFINITIVO:
        // Como 'esTardanza' en el DTO es primitivo (boolean), ya no puede ser null.
        // Podemos usarlo directamente sin miedo al NullPointerException.
        String estadoLetra = estado.isEsTardanza() ? "T" : "P";
        // Nota: Lombok genera el getter como isEsTardanza() o isTardanza() dependiendo del nombre exacto.
        // Si tu campo es "esTardanza", Lombok suele generar "isEsTardanza()". Verifica tu IDE.

        return MarcaRespuestaDto.builder()
                .mensaje(mensajeOracle)
                .tipoMarca(esEntrada ? "ENTRADA" : "SALIDA")
                .horaExacta(esEntrada ? estado.getHoraEntrada() : estado.getHoraSalida())
                .estadoAsistencia(estadoLetra)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaView> obtenerHistorial(String username, LocalDate desde, LocalDate hasta, Pageable pageable) {

        // 1. Obtener Usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. Definir Rango de Fechas por Defecto (Si vienen nulas)
        // Regla: Si no filtra, mostramos el mes actual.
        LocalDate inicio = (desde != null) ? desde : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = (hasta != null) ? hasta : LocalDate.now();

        // 3. Consultar Vista
        return asistenciaViewRepository.findByIdUsuarioAndFechaBetweenOrderByFechaDesc(
                usuario.getIdUsuario(),
                inicio,
                fin,
                pageable
        );
    }
}