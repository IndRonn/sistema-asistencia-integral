package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.asistencia.EstadoAsistenciaDto; // <--- Usamos el nombre correcto
import com.indra.asistencias.dto.asistencia.MarcaRespuestaDto;
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
    public EstadoAsistenciaDto obtenerEstadoDashboard(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return asistenciaRepository.obtenerEstadoActual(usuario.getIdUsuario())
                .orElseGet(() -> EstadoAsistenciaDto.builder()
                        .estado("SIN_MARCAR")
                        .mensaje("Listo para iniciar")
                        .horaEntrada(null)
                        .horaSalida(null)
                        .esTardanza(false) // Valor por defecto seguro
                        .build());
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
}