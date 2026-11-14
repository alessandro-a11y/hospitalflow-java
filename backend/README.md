# HospitalFlow Backend

API REST em Java com Spring Boot para o sistema de gestão hospitalar HospitalFlow.

> **Status:** Backend integrado com módulos de Pacientes, Médicos e Consultas. Módulos de Prontuários e Autenticação pendentes.

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
    │   │   ├── patient/         # Implementado (Entity, Controller, Repository, Service, DTOs)
    │   │   ├── doctor/          # Implementado (Entity, Controller, Repository, Service, DTOs)
    │   │   ├── appointment/     # Implementado (Entity, Controller, Repository, Service, Status, DTOs, Testes)
    │   │   ├── medicalrecord/   # Pendente (apenas estrutura)
    │   │   └── auth/            # Pendente (apenas estrutura)
    │   └── resources/
    │       ├── application.yml
    │       └── logback-spring.xml
    └── test/
        └── java/com/hospitalflow/backend/
            └── appointment/     # AppointmentFlowTest (20 testes)
```

## Módulos implementados

### Pacientes (`/api/patients`)
- **Entidade**: `Patient` (UUID, name, CPF único, birthDate, phone, email)
- **Endpoints**: POST, GET (all), GET (by ID), PUT, DELETE
- **Validação**: CPF único via `@Column(unique=true)` + Jakarta Validation
- **DTOs**: PatientRequest (entrada)

### Médicos (`/api/doctors`)
- **Entidade**: `Doctor` (UUID, name, CRM único, specialty, phone, email)
- **Endpoints**: POST, GET (all), GET (by ID), PUT, DELETE
- **Validação**: CRM único via `@Column(unique=true)` + Jakarta Validation
- **DTOs**: DoctorRequest (entrada)

### Consultas (`/api/appointments`)
- **Entidade**: `Appointment` (UUID, patient, doctor, appointmentDate, reason, status)
- **Status**: SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
- **Endpoints**: POST (schedule), GET (all), PATCH (confirm), PATCH (cancel), PATCH (complete)
- **Regras de negócio**:
  - Impedimento de conflitos de horários para o mesmo médico
  - Cancelamento exige antecedência mínima de 2 horas
  - Transições de status válidas: SCHEDULED → CONFIRMED → COMPLETED, ou CANCELLED a partir de SCHEDULED/CONFIRMED
- **DTOs**: AppointmentRequest, AppointmentResponse
- **Testes**: 20 testes de integração (AppointmentFlowTest)

## Infraestrutura (Alessandro)
- **BaseEntity**: UUID, createdAt, updatedAt (JPA Auditing)
- **ApiResponse**: Wrapper padronizado de resposta
- **GlobalExceptionHandler**: Tratamento centralizado (400, 404, 409, 500)
- **ErrorResponse**: Formato padronizado de erro
- **BusinessRuleException**: Conflitos de negócio (HTTP 409)
- **ResourceNotFoundException**: Recursos não encontrados (HTTP 404)
- **WebConfig**: CORS configurável via `FRONTEND_ORIGIN`
- **OpenApiConfig**: Swagger UI em `/swagger-ui.html`
- **application.yml**: PostgreSQL via variáveis de ambiente, Flyway preparado, H2 para testes

## Requisitos de ambiente

- Java 17 ou superior (JDK)
- Maven 3.9+
- PostgreSQL 15+ (para produção)

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
mvn spring-boot:run
```

A aplicação inicia na porta `8080` (configurável via `SERVER_PORT`).

> **Nota**: Maven wrapper (`mvnw`) não está incluído. Use Maven instalado globalmente.

## Swagger / OpenAPI

Após iniciar a aplicação, acesse:
- Documentação interativa (Swagger UI): `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8080/v3/api-docs`

## Como executar os testes

```bash
cd backend
mvn test
```

**Resultado**: 33 testes passam (20 AppointmentFlowTest + 2 WebConfigTest + 2 BusinessRuleExceptionTest + 2 ErrorResponseTest + 4 GlobalExceptionHandlerTest + 2 ResourceNotFoundExceptionTest + 1 HospitalFlowApplicationTests)

Os testes usam H2 em memória (`spring.jpa.hibernate.ddl-auto=create-drop` no perfil de teste). Não é necessário PostgreSQL para rodar os testes.

## Endpoints disponíveis

| Módulo | Endpoint | Método | Descrição |
|--------|----------|--------|-----------|
| Patients | `/api/patients` | POST | Criar paciente |
| Patients | `/api/patients` | GET | Listar todos |
| Patients | `/api/patients/{id}` | GET | Buscar por ID |
| Patients | `/api/patients/{id}` | PUT | Atualizar |
| Patients | `/api/patients/{id}` | DELETE | Excluir |
| Doctors | `/api/doctors` | POST | Criar médico |
| Doctors | `/api/doctors` | GET | Listar todos |
| Doctors | `/api/doctors/{id}` | GET | Buscar por ID |
| Doctors | `/api/doctors/{id}` | PUT | Atualizar |
| Doctors | `/api/doctors/{id}` | DELETE | Excluir |
| Appointments | `/api/appointments` | POST | Agendar consulta |
| Appointments | `/api/appointments` | GET | Listar todas |
| Appointments | `/api/appointments/{id}/confirm` | PATCH | Confirmar consulta |
| Appointments | `/api/appointments/{id}/cancel` | PATCH | Cancelar consulta |
| Appointments | `/api/appointments/{id}/complete` | PATCH | Finalizar consulta |

## Pendências

### Módulos não implementados
- **Medical Records (Prontuários)**: Apenas estrutura de pacote criada. Falta Entity, Repository, Service, Controller, DTOs.
- **Auth (Autenticação)**: Apenas estrutura de pacote criada. Falta implementação completa (JWT, Spring Security, etc.).

### Melhorias pendentes nos módulos existentes
- **PatientResponse**: DTO de resposta para pacientes (atualmente retorna entidade direta)
- **DoctorResponse**: DTO de resposta para médicos (atualmente retorna entidade direta)
- **Flyway migrations**: Preparado mas desabilitado (`flyway.enabled=false`). Migrações precisam ser criadas quando o banco Neon estiver disponível.
- **Integração Neon**: Banco PostgreSQL no Neon ainda não foi criado/disponibilizado por João Rubens.

### Banco de dados
- Configuração PostgreSQL preservada em `application.yml`
- H2 em memória usado para testes
- `ddl-auto=validate` para produção (Flyway responsável pelo schema)
- Flyway preparado mas desabilitado até migrações serem criadas

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