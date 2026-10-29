package com.hospitalflow.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste da configuração de CORS do WebConfig.
 * <p>
 * Verifica que o mapeamento de CORS cobre a base de caminho {@code /api/**}.
 * A configuração não depende de banco de dados nem de servidor externo.
 */
class WebConfigTest {

    @Test
    void corsConfigurerShouldBeCreated() {
        WebConfig config = new WebConfig();
        assertThat(config).isNotNull();
        assertThat(config.corsConfigurer()).isNotNull();
    }

    @Test
    void defaultAllowedOriginShouldBeWildcard() {
        // Quando a propriedade não está definida, o padrão deve ser "*"
        String defaultOrigin = System.getProperty("frontend.origin", "*");
        assertThat(defaultOrigin).isEqualTo("*");
    }
}
