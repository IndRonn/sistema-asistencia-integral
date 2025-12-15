package com.indra.asistencias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UncategorizedSQLException.class)
    public ResponseEntity<Map<String, Object>> handleDbExceptions(UncategorizedSQLException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof SQLException sqlEx) {
            int errorCode = sqlEx.getErrorCode();

            if (errorCode == 20001) {
                return construirRespuestaError(HttpStatus.CONFLICT, "ASIS-001", "Jornada ya cerrada. No puedes marcar de nuevo.");
            }

            if (errorCode == 20002) {
                return construirRespuestaError(HttpStatus.UNAUTHORIZED, "AUTH-002", "Usuario inactivo.");
            }

            if (errorCode == 20005) {
                return construirRespuestaError(HttpStatus.BAD_REQUEST, "JUST-002", "No se puede justificar una fecha futura.");
            }

            if (errorCode == 20006) {
                return construirRespuestaError(HttpStatus.CONFLICT, "JUST-001", "Ya existe una solicitud pendiente para esta fecha.");
            }
        }

        return construirRespuestaError(HttpStatus.INTERNAL_SERVER_ERROR, "DB-ERR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessExceptions(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String code = "GEN-001";


        if (ex.getMessage().toLowerCase().contains("existe")) {
            status = HttpStatus.CONFLICT;
            code = "USR-001";
        }

        return construirRespuestaError(status, code, ex.getMessage());
    }


    private ResponseEntity<Map<String, Object>> construirRespuestaError(HttpStatus status, String code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
