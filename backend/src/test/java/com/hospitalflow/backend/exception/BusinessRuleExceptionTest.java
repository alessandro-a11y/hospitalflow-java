package com.hospitalflow.backend.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Teste unitário da exceção de regra de negócio (BusinessRuleException).
 */
class BusinessRuleExceptionTest {

    @Test
    void shouldCarryMessageAndBeRuntimeException() {
        BusinessRuleException ex = new BusinessRuleException("Horário em conflito");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Horário em conflito");
    }

    @Test
    void shouldBeThrowingAndCatching() {
        assertThatThrownBy(() -> {
            throw new BusinessRuleException("CPF já cadastrado");
        })
        .isInstanceOf(BusinessRuleException.class)
        .hasMessage("CPF já cadastrado");
    }
}
