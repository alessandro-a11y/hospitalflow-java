package com.hospitalflow.backend.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * <p>
 * Resultado HTTP: {@code 409 Conflict}.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
