# AutoEscola3ESA — API REST para Agendamento de Instruções

API desenvolvida em **Spring Boot (Java 17+)** para gerenciamento de uma auto-escola:
cadastro de instrutores, alunos, usuários (com autenticação via JWT) e agendamento/cancelamento
de instruções.

Disciplina: **SOA e WebServices**
Professor: Carlos Eduardo Machado de Oliveira

## Integrante do grupo

- Victoria Moura — RM555474

## Tecnologias utilizadas

- Java 17+ (projeto atualmente configurado para Java 25)
- Spring Boot 4 (Web, Data JPA, Validation, Security)
- MySQL + Flyway (versionamento de banco de dados)
- JWT (biblioteca `com.auth0:java-jwt`)
- Lombok

## Pré-requisitos

- JDK 17 ou superior instalado
- MySQL em execução localmente (ou ajustar `application.properties`)
- Maven (ou usar o wrapper `./mvnw` incluso no projeto)

## Configuração do banco de dados

Crie um banco chamado `autoescola3esa` no MySQL (o Flyway cria as tabelas
automaticamente na primeira execução, a partir dos scripts em
`src/main/resources/db/migration`).

As credenciais de acesso ficam em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/autoescola3esa
spring.datasource.username=root
spring.datasource.password=fiap
```

Ajuste conforme o ambiente local.

## Como rodar

```bash
./mvnw spring-boot:run
```

A API sobe na porta `8085` (configurável em `application.properties`).

## Cadastro do primeiro usuário administrador

Como o cadastro de usuários via API exige um usuário ADMIN já autenticado
(ver seção de regras de negócio abaixo), o **primeiro usuário** deve ser inserido
diretamente no banco de dados. Utilize a classe utilitária
`src/test/java/br/com/fiap3esa/autoescola3esa/util/Encriptador.java` para gerar
o hash BCrypt da senha desejada e insira manualmente na tabela `usuarios`:

```sql
insert into usuarios (login, senha, perfil) values ('admin', '<hash-gerado>', 'ADMIN');
```

## Autenticação

```
POST /login
{
  "login": "admin",
  "senha": "suasenha"
}
```

Retorna um token JWT que deve ser enviado no header `Authorization: Bearer <token>`
em todas as demais requisições.

## Funcionalidades implementadas

### Instrutores (`/instrutores`) — desenvolvido no CP3
- Cadastro, listagem paginada (10/página, ordenada por nome), detalhamento, atualização
  (nome, telefone, endereço) e exclusão lógica (inativação).
- E-mail, CNH e especialidade não podem ser alterados.

### Alunos (`/alunos`) — implementado no CP4
- `POST /alunos` — cadastro (ADMIN)
- `GET /alunos` — listagem paginada, 10/página, ordenada por nome (ADMIN, USER)
- `GET /alunos/{id}` — detalhamento (ADMIN)
- `PUT /alunos` — atualização de nome, telefone e endereço (ADMIN). E-mail e CPF são imutáveis.
- `DELETE /alunos/{id}` — exclusão lógica / inativação (ADMIN)

### Usuários (`/usuarios`) — implementado no CP4
- `POST /usuarios` — cadastro de usuário, com senha criptografada via BCrypt (ADMIN)
- `GET /usuarios` — listagem de usuários (ADMIN)
- `PUT /usuarios/perfil` — atualização do perfil (ADMIN/USER) de um usuário (ADMIN)
- `DELETE /usuarios/{id}` — exclusão de usuário (ADMIN)
- `PUT /usuarios/senha` — qualquer usuário autenticado pode alterar a própria senha,
  informando a senha atual e a nova senha

### Agendamento de instruções (`/instrucoes`) — desenvolvido em sala (CP3)
- `POST /instrucoes` — agendamento, respeitando: horário de funcionamento (seg. a sáb.,
  06h–21h), duração fixa de 1h, antecedência mínima de 30 min, aluno/instrutor ativos,
  limite de 2 instruções por dia por aluno, disponibilidade do instrutor e escolha
  automática de instrutor quando não informado.

### Cancelamento de instruções — implementado no CP4
- `PUT /instrucoes/{id}/cancelamento` — cancela uma instrução já agendada.
  - Corpo da requisição: `{ "motivo": "ALUNO_DESISTIU" | "INSTRUTOR_CANCELOU" | "OUTROS" }`
  - Regra de negócio: só é permitido cancelar com antecedência mínima de 24 horas
    em relação ao horário agendado da instrução.

## Segurança

- Autenticação via token JWT (stateless).
- Autorização baseada em perfis (`ADMIN`, `USER`) usando `@PreAuthorize`.
- Senhas de usuários armazenadas com hash BCrypt (nunca em texto puro).

## Estrutura do banco de dados (migrations Flyway)

| Versão | Descrição |
|--------|-----------|
| V1 | Criação da tabela `instrutores` |
| V2 | Adiciona coluna `telefone` em `instrutores` |
| V3 | Adiciona coluna `ativo` em `instrutores` |
| V4 | Criação da tabela `usuarios` |
| V5 | Adiciona coluna `perfil` em `usuarios` |
| V6 | Criação da tabela `alunos` |
| V7 | Criação da tabela `instrucoes` |
| V8 | Adiciona coluna `ativo` em `alunos` |
| V9 | Adiciona colunas `cancelada` e `motivo_cancelamento` em `instrucoes` |
