package com.indra.asistencias.exceptions;

import org.springframework.dao.DataIntegrityViolationException; // Importar
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.UncategorizedSQLException; // Importar
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ... (Tus otros manejadores de Auth existen aquí) ...

    // MANEJADOR DE ERRORES DE BASE DE DATOS (Oracle)
    @ExceptionHandler(UncategorizedSQLException.class)
    public ResponseEntity<Map<String, Object>> handleDbExceptions(UncategorizedSQLException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof SQLException sqlEx) {
            int errorCode = sqlEx.getErrorCode();

            // CASO 1: Violación de Regla de Negocio (RAISE_APPLICATION_ERROR)
            // Oracle usa el rango 20000-20999 para errores custom
            if (errorCode == 20001) {
                return construirRespuestaError(
                        HttpStatus.CONFLICT, // 409
                        "ASIS-001",
                        "Jornada ya cerrada. No puedes marcar de nuevo hoy." // Mensaje del Contrato
                );
            }

            if (errorCode == 20002) {
                return construirRespuestaError(
                        HttpStatus.CONFLICT, // 409
                        "ASIS-002",
                        "Usuario inactivo o no autorizado."
                );
            }
        }

        // Error DB desconocido (500)
        return construirRespuestaError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "DB-ERR-GEN",
                "Error interno de base de datos: " + ex.getMessage()
        );
    }

    // Helper para mantener el formato JSON limpio y estándar
    private ResponseEntity<Map<String, Object>> construirRespuestaError(HttpStatus status, String code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}