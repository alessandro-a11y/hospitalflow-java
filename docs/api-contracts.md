# Contratos da API — HospitalFlow

Este documento define os contratos da API REST que todos os integrantes da equipe devem seguir ao implementar seus módulos.

> **Status:** documento de planejamento e alinhamento. Os módulos de domínio ainda não foram implementados.

## Princípios gerais

- **Base path:** todos os endpoints estão sob `/api/`.
- **Formato:** JSON com nomes de propriedades em **camelCase**.
- **Datas:** formato ISO 8601 (ex: `2026-10-28T12:00:00`).
- **IDs:** UUID (identificadores únicos universais).
- **Sem autenticação obrigatória** nesta fase (alinhar estratégia com a equipe antes de adicionar Spring Security).

## Padrões de resposta HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| `200 OK` | Sucesso | Consultas e atualizações bem-sucedidas |
| `201 Created` | Criado | Criação de recurso com sucesso |
| `204 No Content` | Sem conteúdo | Exclusão bem-sucedida |
| `400 Bad Request` | Dados inválidos | Erro de validação ou requisição malformada |
| `404 Not Found` | Não encontrado | Recurso inexistente |
| `409 Conflict` | Conflito | Violação de regra de negócio |

## Formato de erro

Todas as respostas de erro seguem o formato padronizado (implementado em `backend/src/main/java/com/hospitalflow/backend/exception/ErrorResponse.java`):

```json
{
  "timestamp": "2026-10-28T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados inválidos",
  "path": "/api/patients"
}
```

Para erros de validação de campos, o campo `fieldErrors` é incluído:

```json
{
  "timestamp": "2026-10-28T12:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Um ou mais campos estão inválidos",
  "path": "/api/patients",
  "fieldErrors": {
    "name": "não pode ser vazio",
    "email": "e-mail inválido"
  }
}
```

## Endpoints definidos pela equipe

Os seguintes endpoints foram acordados e devem ser implementados pelos respectivos módulos:

| Módulo | Endpoint | Método | Responsável |
|--------|----------|--------|-------------|
| Pacientes | `/api/patients` | GET | (pendente) |
| Pacientes | `/api/patients` | POST | (pendente) |
| Médicos | `/api/doctors` | GET | (pendente) |
| Médicos | `/api/doctors` | POST | (pendente) |
| Consultas | `/api/appointments` | GET | (pendente) |
| Consultas | `/api/appointments` | POST | (pendente) |
| Prontuários | `/api/medical-records` | GET | (pendente) |
| Prontuários | `/api/medical-records` | POST | (pendente) |

> Endpoints de detalhe, atualização e exclusão (ex: `/api/patients/{id}`) devem seguir o mesmo padrão de nomenclatura.

## Proposta de contrato mínimo (pendente de confirmação)

Os campos abaixo são **propostas** baseadas nos requisitos funcionais do projeto original e ainda precisam ser confirmados pela equipe. Não presumimos campos já definidos pelos colegas.

### Pacientes (`/api/patients`)

**Requisição POST:**
```json
{
  "name": "Maria Silva",
  "cpf": "123.456.789-00",
  "email": "maria@email.com",
  "phone": "(11) 99999-9999",
  "birthDate": "1985-03-15"
}
```

**Resposta POST (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Maria Silva",
  "cpf": "123.456.789-00",
  "email": "maria@email.com",
  "phone": "(11) 99999-9999",
  "birthDate": "1985-03-15",
  "createdAt": "2026-10-28T12:00:00",
  "updatedAt": "2026-10-28T12:00:00"
}
```

### Médicos (`/api/doctors`)

**Requisição POST:**
```json
{
  "name": "Dr. João Oliveira",
  "crm": "12345",
  "specialty": "Cardiologia",
  "email": "joao@hospital.com",
  "phone": "(11) 88888-8888"
}
```

**Resposta POST (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Dr. João Oliveira",
  "crm": "12345",
  "specialty": "Cardiologia",
  "email": "joao@hospital.com",
  "phone": "(11) 88888-8888",
  "createdAt": "2026-10-28T12:00:00",
  "updatedAt": "2026-10-28T12:00:00"
}
```

### Consultas (`/api/appointments`)

**Requisição POST:**
```json
{
  "patientId": "550e8400-e29b-41d4-a716-446655440000",
  "doctorId": "550e8400-e29b-41d4-a716-446655440001",
  "appointmentDate": "2026-11-05T14:00:00",
  "reason": "Consulta de rotina"
}
```

**Resposta POST (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "patientId": "550e8400-e29b-41d4-a716-446655440000",
  "doctorId": "550e8400-e29b-41d4-a716-446655440001",
  "appointmentDate": "2026-11-05T14:00:00",
  "reason": "Consulta de rotina",
  "status": "SCHEDULED",
  "createdAt": "2026-10-28T12:00:00",
  "updatedAt": "2026-10-28T12:00:00"
}
```

### Prontuários (`/api/medical-records`)

**Requisição POST:**
```json
{
  "appointmentId": "550e8400-e29b-41d4-a716-446655440002",
  "diagnosis": "Hipertensão arterial",
  "prescription": "Losartana 50mg 1x ao dia",
  "observations": "Paciente responde bem ao tratamento"
}
```

**Resposta POST (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "appointmentId": "550e8400-e29b-41d4-a716-446655440002",
  "diagnosis": "Hipertensão arterial",
  "prescription": "Losartana 50mg 1x ao dia",
  "observations": "Paciente responde bem ao tratamento",
  "createdAt": "2026-10-28T12:00:00",
  "updatedAt": "2026-10-28T12:00:00"
}
```

## Diretrizes para nomeação

- **Endpoints:** kebab-case no path (ex: `/api/medical-records`), substantivos no plural.
- **DTOs:** sufixo `Request` para entrada e `Response` para saída (ex: `PatientRequest`, `PatientResponse`).
- **Campos:** camelCase (ex: `birthDate`, `appointmentDate`, `createdAt`).
- **Entidades:** PascalCase, singular (ex: `Patient`, `Doctor`, `Appointment`, `MedicalRecord`).
- **Relacionamentos:** referencie entidades por `id` (UUID) nos DTOs de requisição; não expanda objetos aninhados a menos que seja estritamente necessário.

## Como os colegas devem integrar

1. Cada módulo implementa seu controller, service, repository e entidade sob seu pacote dedicado (ex: `com.hospitalflow.backend.patient`).
2. As entidades devem estender `com.hospitalflow.backend.common.BaseEntity`.
3. Use `ResourceNotFoundException` para recursos ausentes (HTTP 404).
4. Use `BusinessRuleException` para conflitos de negócio (HTTP 409).
5. Valide entradas com Jakarta Validation (`@Valid`, `@NotNull`, `@Email`, etc.).
6. Registre endpoints sob `/api/...` seguindo esta documentação.
7. Abra um pull request para revisão antes de fazer merge na `main`.

## Swagger / OpenAPI

A documentação interativa está disponível (quando a aplicação estiver rodando) em:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8080/v3/api-docs`
