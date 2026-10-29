package com.hospitalflow.backend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste do tratamento global de exceções da API HospitalFlow.
 * <p>
 * Valida o formato consistente de resposta de erro para exceções de negócio,
 * recurso não encontrado e exceções genéricas, sem expor stack traces ou
 * detalhes internos.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void handleResourceNotFoundShouldReturn404WithStandardFormat() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Paciente não encontrado");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getError()).isEqualTo("Not Found");
        assertThat(body.getMessage()).isEqualTo("Paciente não encontrado");
        assertThat(body.getTimestamp()).isNotNull();
        assertThat(body.getTimestamp().toLocalDate()).isEqualTo(LocalDateTime.now().toLocalDate());
    }

    @Test
    void handleBusinessRuleShouldReturn409WithStandardFormat() {
        BusinessRuleException ex = new BusinessRuleException("Horário de consulta em conflito");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(body.getError()).isEqualTo("Conflict");
        assertThat(body.getMessage()).isEqualTo("Horário de consulta em conflito");
    }

    @Test
    void handleGlobalExceptionShouldReturn500WithoutStackTrace() {
        Exception ex = new RuntimeException("Erro interno inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getError()).isEqualTo("Internal Server Error");
        assertThat(body.getMessage()).isEqualTo("Ocorreu um erro inesperado no servidor");
        // Não deve expor stack trace ou detalhes internos
        assertThat(body.getMessage()).doesNotContain("at ");
        assertThat(body.getMessage()).doesNotContain("Caused by");
    }

    @Test
    void errorResponseShouldIncludeTimestampAndPath() {
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        class TestException extends RuntimeException {
            TestException(String m) { super(m); }
        }

        TestException ex = new TestException("requisição malformada");
        // Simula o fallback genérico para verificar presença de timestamp e path
        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex, request);

        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTimestamp()).isNotNull();
        assertThat(body.getPath()).isNotNull();
    }
}
