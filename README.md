# HospitalFlow

Sistema de gestão hospitalar desenvolvido como projeto acadêmico, com a proposta de ser reimplementado do zero utilizando Java no backend e uma interface web no frontend, aproveitando os requisitos e a ideia do projeto original desenvolvido em C#.

> **Status:** em desenvolvimento / acadêmico. A fundação do backend (infraestrutura compartilhada, configuração, contratos de erro, CORS, OpenAPI e testes) foi implementada. Os módulos de domínio (pacientes, médicos, consultas, prontuários, auth) ainda estão pendentes.

## Objetivo

O HospitalFlow tem como objetivo gerenciar o fluxo de atendimento hospitalar, padronizando o cadastro de pacientes, médicos, consultas, prontuários e usuários do sistema, com foco em organização, rastreabilidade e facilidade de uso por equipes de saúde.

## Funcionalidades planejadas

- Cadastro e gerenciamento de pacientes
- Cadastro e gerenciamento de médicos
- Agendamento e gerenciamento de consultas
- Criação e consulta de prontuários eletrônicos
- Gerenciamento de usuários e controle de acesso
- Contratos de API REST para integração entre backend e frontend

> Os módulos de domínio ainda não foram implementados. A fundação de backend está pronta para receber as implementações dos colegas.

## Tecnologias previstas

- **Backend:** Java 17/21, Spring Boot 3.2.5, Spring Data JPA, PostgreSQL
- **Frontend:** aplicação web (tecnologia a ser definida pela equipe)
- **Banco de dados:** PostgreSQL
- **Build:** Maven (backend) e gerenciador de pacotes a definir (frontend)

## Estrutura do repositório

```
hospitalflow-java/
├── backend/          # API Java com Spring Boot (fundação implementada)
├── frontend/         # Interface web (reservado)
├── docs/             # Documentação técnica, arquitetura, modelo de dados e contratos da API
│   ├── architecture.md
│   └── api-contracts.md
├── .github/
│   └── ISSUE_TEMPLATE/  # Modelos para registrar tarefas e bugs
├── .gitignore        # Arquivos ignorados pelo Git
├── README.md         # Este arquivo
└── LICENSE           # Licença MIT
```

## Backend

O backend está em `backend/` e contém:
- Inicialização da aplicação (`HospitalFlowApplication`)
- Configuração de banco (PostgreSQL via variáveis de ambiente)
- Tratamento global de exceções (`@RestControllerAdvice`)
- Respostas de erro padronizadas (formato JSON consistente)
- Validação de entrada (Jakarta Validation)
- Configuração CORS para o frontend
- Documentação OpenAPI / Swagger UI
- Configuração de logging
- Testes automatizados (JUnit 5)

Veja `backend/README.md` para instruções de execução e contribuição.

## Contratos da API

Os contratos da API estão documentados em `docs/api-contracts.md` e devem ser seguidos por todos os módulos.

## Contribuição

Este é um projeto acadêmico público. Antes de contribuir:

1. Crie uma branch a partir da `main` com um nome descritivo (ex: `feature/nome-da-funcionalidade`).
2. Não faça push direto na `main` sem revisão da equipe.
3. Abra issues para registrar tarefas e bugs usando os modelos em `.github/ISSUE_TEMPLATE/`.
4. Não adicione credenciais, tokens, senhas ou dados reais ao repositório.
5. Preserve o trabalho dos demais integrantes; não sobrescreva implementações alheias.

## Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais informações.
