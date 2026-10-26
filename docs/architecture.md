# Arquitetura — HospitalFlow

## Visão geral

O HospitalFlow é um sistema de gestão hospitalar composto por uma API REST backend (Java + Spring Boot) e uma interface web frontend. O backend expõe contratos de API consumidos pelo frontend, persistindo dados em um banco PostgreSQL.

A camada de backend segue uma organização por camadas (controllers, services, repositories, entities e DTOs), promovendo separação de responsabilidades e facilitando a manutenção e a evolução do sistema.

> **Status:** documento de planejamento. Nenhuma implementação existe ainda.

## Entidades previstas

As seguintes entidades são previstas para o modelo de dados:

1. **Paciente** — dados de identificação e contato do paciente atendido pela instituição.
2. **Médico** — dados de identificação, especialidade e registro profissional do médico.
3. **Consulta** — registro de agendamento/atendimento de um paciente por um médico em uma data/horário.
4. **Prontuário** — prontuário eletrônico associado a uma consulta, contendo observações clínicas e evolução.
5. **Usuário** — credenciais de acesso ao sistema (funcionários da instituição).

## Relacionamentos principais

- Uma **Consulta** pertence a um **Paciente** (muitos-para-um).
- Uma **Consulta** é atendida por um **Médico** (muitos-para-um).
- Um **Prontuário** está associado a uma **Consulta** (um-para-um).
- Um **Usuário** pode realizar operações sobre consultas, pacientes e prontuários (controle de acesso a ser definido).

## Regras de negócio previstas

- Cada consulta deve vincular um paciente e um médico existentes e ativos.
- O horário de uma consulta não deve conflitar com outra consulta do mesmo médico.
- Um prontuário só pode ser criado para uma consulta já realizada ou em andamento.
- Dados de pacientes e prontuários são considerados sensíveis e devem ser acessados apenas por usuários autorizados.

> Estas regras são previsões de planejamento e ainda não foram implementadas.

## Organização proposta do backend

```
backend/
└── src/main/java/com/hospitalflow/
    ├── controller/   # Controllers REST (expõem os contratos da API)
    ├── service/      # Regras de negócio e orquestração
    ├── repository/   # Interfaces Spring Data JPA (acesso a dados)
    ├── entity/       # Entidades JPA mapeadas ao banco de dados
    └── dto/          # Objetos de transferência de dados (requisição/resposta)
```

## Contratos da API a serem definidos

Os contratos de API REST ainda estão em definição e devem ser compartilhados com a equipe antes do início da implementação. Exemplo de endpoints previstos (sujeitos a alteração):

- `GET /pacientes`, `POST /pacientes`, `GET /pacientes/{id}`, `PUT /pacientes/{id}`, `DELETE /pacientes/{id}`
- `GET /medicos`, `POST /medicos`, `GET /medicos/{id}`, `PUT /medicos/{id}`, `DELETE /medicos/{id}`
- `GET /consultas`, `POST /consultas`, `GET /consultas/{id}`, `PUT /consultas/{id}`, `DELETE /consultas/{id}`
- `GET /prontuarios`, `POST /prontuarios`, `GET /prontuarios/{id}`, `PUT /prontuarios/{id}`
- `GET /usuarios`, `POST /usuarios`, `GET /usuarios/{id}`

Os contratos finais (formatos de requisição, resposta, códigos de status e autenticação) serão documentados em `docs/` e acordados com a equipe.
