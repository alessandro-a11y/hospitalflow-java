# API de Médicos

Base: `/api/doctors`

## Cadastrar médico
`POST /api/doctors`

```json
{
  "name": "Dra. Ana Souza",
  "crm": "12345-AL",
  "specialty": "Cardiologia",
  "phone": "82999990000",
  "email": "ana@hospitalflow.com"
}
```

Resposta: `201 Created`, com o médico cadastrado e o `id` (UUID).
Erro: CRM já cadastrado, ou campos obrigatórios vazios (`name`, `crm`, `specialty`).

## Listar médicos
`GET /api/doctors`

Resposta: `200 OK`, lista de médicos.

## Buscar médico por ID
`GET /api/doctors/{id}`

Resposta: `200 OK`. Erro quando o médico não existe: "Médico não encontrado".

## Atualizar médico
`PUT /api/doctors/{id}`

Corpo igual ao do cadastro. Resposta: `200 OK`, com o médico atualizado.
Erro: CRM já usado por outro médico.