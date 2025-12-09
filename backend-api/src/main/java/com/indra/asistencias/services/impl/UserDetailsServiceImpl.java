package com.indra.asistencias.services.impl;

import com.indra.asistencias.models.Usuario; // <--- TU ENTIDAD ORACLE
import com.indra.asistencias.repositories.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // <--- EL OBJETO DE SPRING
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. BUSCAMOS TU ENTIDAD (Datos de Oracle)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. CONVERTIMOS ENTIDAD -> USER DETAILS (Adaptador)
        // Aquí tomamos los datos de TU 'Usuario' y llenamos el 'User' de Spring.
        return new User(
                usuario.getUsername(),        // Nombre de usuario
                usuario.getPassword(),        // La contraseña hasheada (BCrypt)
                usuario.getEstado().equals("A"), // enabled: Si estado es "A", entra. Si es "I", bloqueado.
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                // Convertimos tu Rol simple ("ADMIN") en una Autoridad ("ROLE_ADMIN")
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}