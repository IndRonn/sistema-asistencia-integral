package com.indra.asistencias.services.impl;

import com.indra.asistencias.models.Usuario;
import com.indra.asistencias.repositories.UsuarioRepository;
import com.indra.asistencias.security.UserDetailsImpl; // <--- Import correcto (Búnker)
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));


        String rolDb = Optional.ofNullable(usuario.getRol()).orElse("EMPLEADO");
        String rolSpring = rolDb.startsWith("ROLE_") ? rolDb : "ROLE_" + rolDb;

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(rolSpring)
        );


        return new UserDetailsImpl(
                usuario.getIdUsuario(),         // ID
                usuario.getUsername(),          // Username
                usuario.getEmail(),             // Email
                usuario.getPassword(),          // Password Hash
                authorities,                    // Roles
                "A".equals(usuario.getEstado()) // Enabled
        );
    }
}