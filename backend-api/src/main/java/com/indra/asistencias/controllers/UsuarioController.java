package com.indra.asistencias.controllers;

import com.indra.asistencias.dto.usuario.CambioEstadoDto;
import com.indra.asistencias.dto.usuario.UsuarioRequestDto;
import com.indra.asistencias.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String search, Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(search, pageable));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody CambioEstadoDto dto) {
        String nuevoEstado = usuarioService.cambiarEstado(id, dto);
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado correctamente", "nuevoEstado", nuevoEstado));
    }
}