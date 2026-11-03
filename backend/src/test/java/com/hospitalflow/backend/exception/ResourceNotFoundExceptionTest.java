package com.hospitalflow.backend.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Teste unitário da exceção de recurso não encontrado (ResourceNotFoundException).
 */
class ResourceNotFoundExceptionTest {

    @Test
    void shouldCarryMessageAndBeRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Médico não encontrado");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Médico não encontrado");
    }

    @Test
    void shouldBeThrowingAndCatching() {
        assertThatThrownBy(() -> {
            throw new ResourceNotFoundException("Paciente não encontrado");
        })
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Paciente não encontrado");
    }
}
