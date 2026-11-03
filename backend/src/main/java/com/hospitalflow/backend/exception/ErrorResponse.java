package com.hospitalflow.backend.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Modelo de resposta de erro padronizado da API HospitalFlow.
 * <p>
 * Exemplo:
 * <pre>
 * {
 *   "timestamp": "2026-10-28T12:00:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Dados inválidos",
 *   "path": "/api/patients"
 * }
 * </pre>
 */
@Getter
@Setter
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    public ErrorResponse(LocalDateTime timestamp, int status, String error,
                         String message, String path, Map<String, String> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }
}
