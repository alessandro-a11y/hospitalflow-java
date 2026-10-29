package com.hospitalflow.backend.exception;

/**
 * Exceção lançada quando um recurso solicitado não existe no banco de dados.
 * <p>
 * Resultado HTTP: {@code 404 Not Found}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
