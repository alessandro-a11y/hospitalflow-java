package com.hospitalflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal de inicialização da aplicação HospitalFlow.
 * <p>
 * Esta classe é o ponto de entrada da API Spring Boot. Ela configura e inicia
 * o contexto da aplicação, incluindo os módulos compartilhados de infraestrutura
 * (exceções, validação, CORS, OpenAPI, banco de dados e logging).
 * </p>
 *
 * <p>Esta fundação deve ser preservada pelos demais integrantes da equipe.
 * Os módulos de domínio (pacientes, médicos, consultas, prontuários, auth)
 * devem ser adicionados como pacotes separados sob {@code com.hospitalflow.backend}.</p>
 */
@SpringBootApplication
public class HospitalFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalFlowApplication.class, args);
    }
}
