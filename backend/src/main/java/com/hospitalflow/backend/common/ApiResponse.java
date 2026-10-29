package com.hospitalflow.backend.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Resposta de sucesso genérica da API.
 * <p>
 * Utilizada quando não há um DTO específico de retorno, mas é necessário
 * informar ao cliente que a operação foi bem-sucedida com uma mensagem.
 * </p>
 */
@Getter
@Setter
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, Object data) {
        this(true, message, data);
    }

    public ApiResponse(String message) {
        this(true, message, null);
    }
}
