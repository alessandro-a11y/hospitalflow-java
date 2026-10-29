package com.hospitalflow.backend.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste unitário do modelo de resposta de erro padronizado (ErrorResponse).
 */
class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 10, 28, 12, 0, 0);
        Map<String, String> fieldErrors = Map.of("name", "não pode ser vazio");

        ErrorResponse error = new ErrorResponse(
                timestamp, 400, "Bad Request", "Dados inválidos", "/api/patients", fieldErrors);

        assertThat(error.getTimestamp()).isEqualTo(timestamp);
        assertThat(error.getStatus()).isEqualTo(400);
        assertThat(error.getError()).isEqualTo("Bad Request");
        assertThat(error.getMessage()).isEqualTo("Dados inválidos");
        assertThat(error.getPath()).isEqualTo("/api/patients");
        assertThat(error.getFieldErrors()).containsEntry("name", "não pode ser vazio");
    }

    @Test
    void shouldCreateErrorResponseWithoutFieldErrors() {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), 404, "Not Found", "Recurso não encontrado", "/api/doctors/1", null);

        assertThat(error.getFieldErrors()).isNull();
        assertThat(error.getStatus()).isEqualTo(404);
    }
}
