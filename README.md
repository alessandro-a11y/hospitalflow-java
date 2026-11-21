# HospitalFlow Backend

API REST em Java com Spring Boot para o sistema de gestão hospitalar HospitalFlow.

> **Status:** Backend com módulos de Pacientes, Médicos, Consultas e Prontuários Médicos implementados. Módulo de Autenticação pendente.

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
├── .env.example
├── database/
│   └── hospitalflow_schema.sql   # Script do esquema PostgreSQL
└── src/
    ├── main/
    │   ├── java/com/hospitalflow/backend/
    │   │   ├── HospitalFlowApplication.java
    │   │   ├── config/          # WebConfig (CORS), OpenApiConfig (Swagger), JpaAuditConfig
    │   │   ├── exception/       # GlobalExceptionHandler, ErrorResponse, ResourceNotFoundException, BusinessRuleException
    │   │   ├── common/          # ApiResponse, BaseEntity
    │   │   ├── controller/      # Controllers REST (Patient, Doctor, Appointment, MedicalRecord)
    │   │   ├── service/         # Regras de negócio
    │   │   ├── repository/      # Repositórios JPA (Spring Data)
    │   │   ├── entity/          # Entidades JPA e enum AppointmentStatus
    │   │   ├── dto/             # Requests e responses (records)
    │   │   └── auth/            # Pendente (apenas estrutura)
    │   └── resources/
    │       ├── application.yml
    │       └── logback-spring.xml
    └── test/
        └── java/com/hospitalflow/backend/
            ├── HospitalFlowApplicationTests.java
            ├── integration/     # AppointmentFlowTest
            ├── config/          # WebConfigTest
            └── exception/       # Testes de exceções, ErrorResponse e GlobalExceptionHandler
```

## Módulos implementados

### Pacientes (`/api/patients`)
- **Entidade**: `Patient` (UUID, name, CPF único, birthDate, phone, email)
- **Endpoints**: POST, GET (all), GET (by ID)
- **Validação**: CPF único via `@Column(unique=true)` + Jakarta Validation
- **DTOs**: PatientRequest (entrada)

### Médicos (`/api/doctors`)
- **Entidade**: `Doctor` (UUID, name, CRM único, specialty, phone, email)
- **Endpoints**: POST, GET (all), GET (by ID), PUT
- **Validação**: CRM único via `@Column(unique=true)` + Jakarta Validation
- **DTOs**: DoctorRequest (entrada)

### Consultas (`/api/appointments`)
- **Entidade**: `Appointment` (UUID, patient, doctor, appointmentDate, reason, status)
- **Status**: SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
- **Endpoints**:
  - POST (schedule)
  - GET (all com filtros: patientId, doctorId, status, startDate, endDate)
  - PATCH (confirm)
  - PATCH (cancel)
  - PATCH (complete)
  - PATCH (reschedule)
- **Regras de negócio**:
  - Impedimento de conflitos de horários para o mesmo médico **e mesmo paciente**
  - Consultas canceladas não geram conflito
  - Cancelamento exige antecedência mínima de 2 horas
  - Transições de status válidas encapsuladas na entidade: SCHEDULED → CONFIRMED → COMPLETED, ou CANCELLED a partir de SCHEDULED/CONFIRMED
  - Reagendamento permitido apenas para SCHEDULED/CONFIRMED
- **DTOs**: AppointmentRequest, AppointmentResponse (records)
- **Testes**: 35 testes de integração (AppointmentFlowTest)

### Prontuários Médicos (`/api/medical-records`)
- **Entidade**: `MedicalRecord` (UUID, patient, doctor, appointment opcional, recordDate, diagnosis, notes)
- **Endpoints**: POST (create), GET (by ID), GET (by patientId), GET (all)
- **Regras de negócio**:
  - Prontuário associado a paciente e médico
  - Associação opcional a consulta (apenas consultas COMPLETED)
  - Validação de que consulta pertence ao mesmo paciente e médico
- **DTOs**: MedicalRecordRequest, MedicalRecordResponse

## Infraestrutura (Alessandro)
- **BaseEntity**: classe abstrata com UUID, createdAt, updatedAt (JPA Auditing)
- **ApiResponse**: Wrapper padronizado de resposta
- **GlobalExceptionHandler**: Tratamento centralizado (400, 404, 409, 500)
- **ErrorResponse**: Formato padronizado de erro
- **BusinessRuleException**: Conflitos de negócio (HTTP 409)
- **ResourceNotFoundException**: Recursos não encontrados (HTTP 404)
- **WebConfig**: CORS configurável via `FRONTEND_ORIGIN`
- **OpenApiConfig**: Swagger UI em `/swagger-ui.html`
- **application.yml**: PostgreSQL via variáveis de ambiente, Flyway preparado (desabilitado)

## Requisitos de ambiente

- Java 17 ou superior (JDK)
- Maven 3.9+ instalado (o projeto não inclui Maven wrapper)
- PostgreSQL 15+ (apenas para executar a aplicação; os testes não precisam dele)

## Configuração do PostgreSQL

1. Instale o PostgreSQL e crie um banco de dados chamado `hospitalflow`:
   ```sql
   CREATE DATABASE hospitalflow;
   ```

2. Crie as tabelas executando o script `database/hospitalflow_schema.sql` no banco `hospitalflow`.
   Isso é necessário porque o Flyway está desabilitado e o Hibernate está em `ddl-auto=validate`:
   a aplicação **não cria** as tabelas sozinha e não sobe se elas não existirem.

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

> O arquivo `.env.example` serve apenas como referência dos nomes das variáveis. O Spring Boot não lê um arquivo `.env` automaticamente: as variáveis precisam estar definidas no ambiente (como acima) ou na configuração de execução da IDE.
>
> Nunca commite o arquivo `.env`. Ele está ignorado pelo `.gitignore`.

## Banco de dados do projeto (Neon)

O banco usado pelo grupo é um PostgreSQL hospedado no Neon, acessível pela internet. Para apontar a aplicação para ele, use a connection string do painel do Neon (botão **Connect**) no formato JDBC, com SSL e sem o parâmetro `channel_binding`:

```powershell
$env:DB_URL="jdbc:postgresql://<host-do-neon>/<nome-do-banco>?sslmode=require"
$env:DB_USERNAME="<usuario>"
$env:DB_PASSWORD="<senha>"
```

As tabelas (`patients`, `doctors`, `appointments` e `medical_records`) precisam existir antes da primeira execução. Elas são criadas com o script `database/hospitalflow_schema.sql`, executado no SQL Editor do Neon ou pelo `psql`. O script não usa `IF NOT EXISTS`: se parte das tabelas já existir, execute só os blocos que faltam. Se a aplicação parar com `Schema-validation: missing table [...]`, é essa a causa.

Nunca registre a senha do banco em arquivo versionado, slide ou mensagem. Ela deve existir apenas nas variáveis de ambiente.

## Como executar a aplicação

```bash
cd backend
mvn spring-boot:run
```

A aplicação inicia na porta `8081`, definida em `server.port` no `application.yml`.

## Swagger / OpenAPI

Após iniciar a aplicação, acesse:
- Documentação interativa (Swagger UI): `http://localhost:8081/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8081/v3/api-docs`

## Como executar os testes

```bash
cd backend
mvn test
```

**Resultado**: 48 testes, 0 falhas, `BUILD SUCCESS` (execução de 15/11/2026, cerca de 1min35).

Distribuição: 35 `AppointmentFlowTest` + 2 `WebConfigTest` + 2 `BusinessRuleExceptionTest` + 2 `ErrorResponseTest` + 4 `GlobalExceptionHandlerTest` + 2 `ResourceNotFoundExceptionTest` + 1 `HospitalFlowApplicationTests`.

Os testes usam H2 em memória, configurado nas próprias classes de teste com `@TestPropertySource` (`spring.jpa.hibernate.ddl-auto=create-drop`). Não é necessário PostgreSQL para rodar os testes.

## Endpoints disponíveis

| Módulo | Endpoint | Método | Descrição |
|--------|----------|--------|-----------|
| Patients | `/api/patients` | POST | Criar paciente |
| Patients | `/api/patients` | GET | Listar todos |
| Patients | `/api/patients/{id}` | GET | Buscar por ID |
| Doctors | `/api/doctors` | POST | Criar médico |
| Doctors | `/api/doctors` | GET | Listar todos |
| Doctors | `/api/doctors/{id}` | GET | Buscar por ID |
| Doctors | `/api/doctors/{id}` | PUT | Atualizar |
| Appointments | `/api/appointments` | POST | Agendar consulta |
| Appointments | `/api/appointments` | GET | Listar todas (com filtros: patientId, doctorId, status, startDate, endDate) |
| Appointments | `/api/appointments/{id}/confirm` | PATCH | Confirmar consulta |
| Appointments | `/api/appointments/{id}/cancel` | PATCH | Cancelar consulta |
| Appointments | `/api/appointments/{id}/complete` | PATCH | Finalizar consulta |
| Appointments | `/api/appointments/{id}/reschedule` | PATCH | Reagendar consulta |
| Medical Records | `/api/medical-records` | POST | Criar prontuário |
| Medical Records | `/api/medical-records` | GET | Listar todos |
| Medical Records | `/api/medical-records/{id}` | GET | Buscar por ID |
| Medical Records | `/api/medical-records/patient/{patientId}` | GET | Listar por paciente |

## Roteiro rápido de verificação

Com a aplicação em execução, abra um segundo terminal (PowerShell) e rode o script abaixo. Ele cria um paciente e um médico com valores únicos de CPF e CRM, agenda uma consulta e percorre o ciclo de vida dela. Foi executado em 15/11/2026 contra o banco Neon, com o resultado descrito depois do script.

```powershell
$base = "http://localhost:8081"

function Call($method, $path, $body = $null) {
    try {
        if ($body) {
            return Invoke-RestMethod -Method $method -Uri "$base$path" -ContentType "application/json" -Body ($body | ConvertTo-Json)
        }
        return Invoke-RestMethod -Method $method -Uri "$base$path"
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        Write-Host "  ERRO $code -> $($_.ErrorDetails.Message)" -ForegroundColor Yellow
    }
}

$cpf = -join (1..11 | ForEach-Object { Get-Random -Maximum 10 })
$crm = "CRM-" + (Get-Random -Maximum 99999)
$data = (Get-Date).AddDays(30).Date.AddHours(10).ToString("yyyy-MM-ddTHH:mm:ss")

$p = Call POST "/api/patients" @{ name="Paciente Teste POO"; cpf=$cpf; birthDate="2000-05-20"; phone="82999990000"; email="paciente.teste@email.com" }
$d = Call POST "/api/doctors" @{ name="Dra. Teste POO"; crm=$crm; specialty="Clinica Geral"; phone="82988880000"; email="medica.teste@email.com" }
$a = Call POST "/api/appointments" @{ patientId=$p.id; doctorId=$d.id; appointmentDate=$data; reason="Consulta de rotina" }
Call PATCH "/api/appointments/$($a.id)/confirm"      # CONFIRMED
Call PATCH "/api/appointments/$($a.id)/confirm"      # 409
Call POST "/api/appointments" @{ patientId=$p.id; doctorId=$d.id; appointmentDate=$data; reason="Mesmo horario" }   # 409
Call POST "/api/patients" @{ name="CPF Curto"; cpf="123"; birthDate="2000-05-20" }   # 400
Call PATCH "/api/appointments/$($a.id)/complete"     # COMPLETED
Call POST "/api/medical-records" @{ patientId=$p.id; doctorId=$d.id; appointmentId=$a.id; recordDate=$data; diagnosis="Exemplo de diagnostico"; notes="Observacoes de teste" }
```

Resultado obtido: a consulta passa de `SCHEDULED` para `CONFIRMED` e para `COMPLETED`; a segunda confirmação retorna 409 (`Apenas consultas agendadas podem ser confirmadas`); o agendamento no mesmo horário retorna 409 (`Médico já possui consulta neste horário`); o CPF com tamanho inválido retorna 400; o prontuário é criado associado à consulta finalizada.

Os mesmos passos podem ser feitos pelo Swagger UI (`http://localhost:8081/swagger-ui.html`). Os dados criados ficam gravados no banco, que é compartilhado pelo grupo.

## Pendências

### Módulos não implementados
- **Auth (Autenticação)**: Apenas estrutura de pacote criada. Falta implementação completa (JWT, Spring Security, etc.).

### Melhorias pendentes nos módulos existentes
- **Pacientes**: endpoints PUT (atualizar) e DELETE (excluir) ainda não implementados.
- **Médicos**: endpoint DELETE (excluir) ainda não implementado.
- **PatientResponse**: DTO de resposta para pacientes (atualmente retorna entidade direta)
- **DoctorResponse**: DTO de resposta para médicos (atualmente retorna entidade direta)
- **Flyway migrations**: Preparado mas desabilitado (`flyway.enabled=false`). Migrações precisam ser criadas quando o banco Neon estiver disponível.
- **Integração Neon**: Banco PostgreSQL no Neon ainda não foi criado/disponibilizado por João Rubens.
- **Listagem de consultas (`GET /api/appointments`)**: retorna erro 500 quando a aplicação roda contra o PostgreSQL (Neon), com ou sem filtros. Os testes passam porque usam H2. A causa provável está na consulta `findWithFilters` do `AppointmentRepository` (filtros opcionais com parâmetros nulos), mas ainda não foi confirmada pelo log do servidor. Os demais endpoints de listagem (pacientes, médicos e prontuários) funcionam.
- **`.env.example`**: ainda traz `SERVER_PORT=8080`, valor que o `application.yml` não lê (a porta é fixa em 8081).

### Banco de dados
- Configuração PostgreSQL preservada em `application.yml`
- H2 em memória usado apenas nos testes
- `ddl-auto=validate` para execução normal: o esquema deve existir antes (script em `database/hospitalflow_schema.sql`)
- Flyway preparado mas desabilitado até migrações serem criadas

## Como criar novas funcionalidades

1. Adicione as classes do novo módulo em cada camada de `com.hospitalflow.backend`: `controller`, `service`, `repository`, `entity` e `dto`.
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