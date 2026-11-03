package com.hospitalflow.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI (Swagger UI).
 * <p>
 * Disponibiliza a documentação da API em {@code /swagger-ui.html} e a
 * especificação OpenAPI em {@code /v3/api-docs}.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hospitalFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HospitalFlow API")
                        .description("API REST do sistema de gestão hospitalar HospitalFlow. " +
                                "Projeto acadêmico em desenvolvimento.")
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("Equipe HospitalFlow")
                                .url("https://github.com/alessandro-a11y/hospitalflow-java"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
