package com.hospitalflow.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS para integração com o frontend.
 * <p>
 * A origem permitida é lida da propriedade {@code frontend.origin} (definida via
 * variável de ambiente {@code FRONTEND_ORIGIN}), com fallback para {@code *}
 * apenas em ambiente de desenvolvimento. Nunca defina uma origem de produção
 * hardcoded neste arquivo.
 * </p>
 */
@Configuration
public class WebConfig {

    private static final String DEFAULT_ALLOWED_ORIGIN = "*";

    /**
     * Configura CORS permitindo chamadas da origem do frontend.
     *
     * @return configurador de CORS do Spring MVC
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String allowedOrigin = System.getProperty("frontend.origin", DEFAULT_ALLOWED_ORIGIN);
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigin)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(!DEFAULT_ALLOWED_ORIGIN.equals(allowedOrigin));
            }
        };
    }
}
