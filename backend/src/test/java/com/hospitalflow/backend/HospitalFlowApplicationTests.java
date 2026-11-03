package com.hospitalflow.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de inicialização e configuração básica da aplicação HospitalFlow.
 * <p>
 * Verifica se o contexto Spring sobe corretamente sem depender de conexão
 * com banco de dados externo (usa propriedades de teste em memória).
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
class HospitalFlowApplicationTests {

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
        assertThat(context.getBean(HospitalFlowApplication.class)).isNotNull();
    }
}
