# HospitalFlow

Sistema de gestão hospitalar desenvolvido como projeto acadêmico, com a proposta de ser reimplementado do zero utilizando Java no backend e uma interface web no frontend, aproveitando os requisitos e a ideia do projeto original desenvolvido em C#.

> **Status:** em desenvolvimento / acadêmico. Nenhuma funcionalidade está implementada nesta versão inicial. Este repositório contém somente a estrutura de pastas, documentação inicial e configurações base.

## Objetivo

O HospitalFlow tem como objetivo gerenciar o fluxo de atendimento hospitalar, padronizando o cadastro de pacientes, médicos, consultas, prontuários e usuários do sistema, com foco em organização, rastreabilidade e facilidade de uso por equipes de saúde.

## Funcionalidades planejadas

- Cadastro e gerenciamento de pacientes
- Cadastro e gerenciamento de médicos
- Agendamento e gerenciamento de consultas
- Criação e consulta de prontuários eletrônicos
- Gerenciamento de usuários e controle de acesso
- Contratos de API REST para integração entre backend e frontend

> Estas funcionalidades estão **planejadas** e ainda não foram implementadas.

## Tecnologias previstas

- **Backend:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL
- **Frontend:** aplicação web (tecnologia a ser definida pela equipe)
- **Banco de dados:** PostgreSQL
- **Build:** Maven (backend) e gerenciador de pacotes a definir (frontend)

## Estrutura do repositório

```
hospitalflow-java/
├── backend/          # API Java com Spring Boot (reservado)
├── frontend/         # Interface web (reservado)
├── docs/             # Documentação técnica, arquitetura, modelo de dados e contratos da API
├── .github/
│   └── ISSUE_TEMPLATE/  # Modelos para registrar tarefas e bugs
├── .gitignore        # Arquivos ignorados pelo Git
├── README.md         # Este arquivo
└── LICENSE           # Licença MIT
```

## Contribuição

Este é um projeto acadêmico público. Antes de contribuir:

1. Crie uma branch a partir da `main` com um nome descritivo (ex: `feature/nome-da-funcionalidade`).
2. Não faça push direto na `main` sem revisão da equipe.
3. Abra issues para registrar tarefas e bugs usando os modelos em `.github/ISSUE_TEMPLATE/`.
4. Não adicione credenciais, tokens, senhas ou dados reais ao repositório.

## Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais informações.
