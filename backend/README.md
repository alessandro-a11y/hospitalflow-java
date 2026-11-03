# HospitalFlow Backend

API REST em Java com Spring Boot para o sistema de gestão hospitalar HospitalFlow.

> **Status:** fundação da infraestrutura implementada. Os módulos de domínio
> (pacientes, médicos, consultas, prontuários, auth) ainda não foram implementados
> e são responsabilidade dos demais integrantes da equipe.

## Stack tecnológica

- Java 17 (compatível com Java 21)
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA + Hibernate
- PostgreSQL
- Jakarta Validation
- Springdoc OpenAPI (Swagger UI)
- Flyway (preparado para migrações)
- Maven
- JUnit 5

## Estrutura

```
backend/
├── pom.xml
├── README.md
├── .env.example
└── src/
    ├── main/
    │   ├── java/com/hospitalflow/backend/
    │   │   ├── HospitalFlowApplication.java
    │   │   ├── config/          # WebConfig (CORS), OpenApiConfig (Swagger), JpaAuditConfig
    │   │   ├── exception/       # GlobalExceptionHandler, ErrorResponse, ResourceNotFoundException, BusinessRuleException
    │   │   ├── common/          # ApiResponse, BaseEntity
    │   │   ├── patient/         # Reservado (módulo de pacientes)
    │   │   ├── doctor/          # Reservado (módulo de médicos)
    │   │   ├── appointment/     # Reservado (módulo de consultas)
    │   │   ├── medicalrecord/   # Reservado (módulo de prontuários)
    │   │   └── auth/            # Reservado (módulo de autenticação)
    │   └── resources/
    │       ├── application.yml
    │       └── logback-spring.xml
    └── test/
        └── java/com/hospitalflow/backend/
```

## Requisitos de ambiente

- Java 17 ou superior (JDK)
- Maven 3.9+
- PostgreSQL 15+

## Configuração do PostgreSQL

1. Instale o PostgreSQL e crie um banco de dados chamado `hospitalflow`:
   ```sql
   CREATE DATABASE hospitalflow;
   ```

2. Copie `.env.example` para `.env` e preencha as credenciais:
   ```bash
   cp .env.example .env
   ```

3. Defina as variáveis de ambiente antes de executar a aplicação (exemplo no Linux/macOS):
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/hospitalflow
   export DB_USERNAME=postgres
   export DB_PASSWORD=sua_senha
   export FRONTEND_ORIGIN=http://localhost:5173
   ```

   No Windows (PowerShell):
   ```powershell
   $env:DB_URL="jdbc:postgresql://localhost:5432/hospitalflow"
   $env:DB_USERNAME="postgres"
   $env:DB_PASSWORD="sua_senha"
   $env:FRONTEND_ORIGIN="http://localhost:5173"
   ```

> Nunca commite o arquivo `.env`. Ele está ignorado pelo `.gitignore`.

## Como executar a aplicação

```bash
cd backend
./mvnw spring-boot:run
# ou, se o Maven estiver instalado globalmente:
mvn spring-boot:run
```

A aplicação inicia na porta `8080` (configurável via `SERVER_PORT`).

## Swagger / OpenAPI

Após iniciar a aplicação, acesse:
- Documentação interativa (Swagger UI): `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8080/v3/api-docs`

## Como executar os testes

```bash
cd backend
mvn test
```

Os testes unitários são independentes de banco de dados (usam H2 em memória quando configurado
ou mocks). Não é necessário PostgreSQL para rodar os testes da fundação.

## Como criar novas funcionalidades

1. Crie um pacote de domínio sob `com.hospitalflow.backend.<modulo>` (ex: `patient`, `doctor`).
2. As entidades devem estender `com.hospitalflow.backend.common.BaseEntity`.
3. Use `ResourceNotFoundException` para recursos ausentes (HTTP 404) e
   `BusinessRuleException` para conflitos de negócio (HTTP 409).
4. Valide entradas com Jakarta Validation (`@Valid`, `@NotNull`, etc.).
5. Registre endpoints sob `/api/...` seguindo os contratos em `docs/api-contracts.md`.
6. Abra um pull request para revisão da equipe antes de fazer merge na `main`.

## Trabalhando com branches e pull requests

1. Sempre crie uma branch a partir da `main` com um nome descritivo:
   `git checkout -b feature/nome-da-funcionalidade`
2. Não faça push direto na `main` sem revisão da equipe.
3. Preserve as branches dos demais integrantes; não exclua, renomeie ou faça checkout
   de branches alheias.
4. Abra um pull request para integrar seu módulo na `main` após revisão.
5. A branch `feature/alessandro-integration` é usada exclusivamente para coordenação
   de integração, configuração do backend e contratos da API.

## Licença

MIT. Veja o arquivo `LICENSE` na raiz do repositório.
