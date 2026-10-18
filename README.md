# Itinerário — backend de mentoria acadêmica

Implementação inicial do [SDD](SDD_plataforma_mentoria_aprendizagem.md), adaptada para **Java 17** por solicitação do projeto.

## Stack

- Java 17, Spring Boot 3.5, Spring MVC e Maven.
- Spring Data JPA/Hibernate, PostgreSQL e migrations SQL com Flyway.
- Spring Security, senhas PBKDF2, sessões HTTP-only, CSRF e validação Jakarta Bean Validation.
- JUnit 5, MockMvc e Spring Security Test. H2 em modo PostgreSQL somente nos testes locais.
- Caffeine mantém contadores limitados de autenticação em memória (10 requisições/minuto/IP); não é cache de dados da aplicação.

O frontend Next.js + TypeScript ainda não foi criado. Sua implementação independente, inicialmente com dados simulados, está especificada no [SDD de frontend](SDD_frontend_plataforma_mentoria_aprendizagem.md).

O primeiro marco do frontend está disponível em [`frontend/`](frontend/README.md): uma demonstração mobile-first da área de aprendizagem do estudante, com cinco matérias, atividades gamificadas e progresso persistido localmente. Mentorias e áreas de mentor e administrador permanecem pendentes.

## Executar

Com Docker e Docker Compose instalados:

1. Copie `.env.example` para `.env` e defina `DATABASE_PASSWORD`.
2. Execute `docker compose up --build`.
3. API em `http://localhost:8080`; verifique `GET /health`.

O Compose é exclusivamente local. Em produção, use HTTPS, `COOKIE_SECURE=true` (padrão), segredo externo e configuração de proxy confiável. As sessões são locais à instância, expiram após 30 minutos sem atividade e são perdidas ao reiniciar.

Sem Docker: instale JDK 17 e Maven 3.9, disponibilize PostgreSQL e configure `DATABASE_URL` (JDBC), `DATABASE_USER`, `DATABASE_PASSWORD`. Você pode colocar esses valores no `.env` da raiz, que a aplicação lê automaticamente quando iniciada nessa pasta. Use valores sem aspas; variáveis de ambiente têm prioridade. Para HTTP local, defina `COOKIE_SECURE=false`. Execute `mvn spring-boot:run`. O Flyway aplica migrations ao iniciar; Hibernate apenas valida o schema.

O arquivo `.env` é local e ignorado pelo Git. `.env.example` contém somente nomes de configurações e valores não secretos. Não é necessário enviar a senha no chat.

Para preparar seu próprio banco, use [database/01_create_database.sql](database/01_create_database.sql) e siga [a configuração do banco e do primeiro administrador](database/README.md). Execute o bootstrap administrativo antes do primeiro cadastro. Não aplique migrations manualmente.

## Testes e build

```sh
mvn verify
```

Testes cobrem autenticação, permissões, aprovação/rejeição, edição de perfis, matching, isolamento entre participantes, bloqueio, transições, auditoria, aceite simultâneo, restrição de contato pessoal e bootstrap administrativo. Ainda é necessário validar migrations e integração com PostgreSQL real (Testcontainers previsto); H2 não substitui essa validação.

Também cobrem atividades, feedback, pontuação sem duplicidade, concorrência de tentativas, progresso, conquistas, conteúdo inativo e seed educacional. Validação mais recente: **21 testes aprovados**.

## Formatação e histórico de mudanças

Com Node.js e npm instalados, execute `npm ci` para instalar as versões fixadas no lockfile:

```sh
npm run format
npm run format:check
```

Prettier formata Java, XML, JSON, YAML e Markdown. Java usa 4 espaços e os demais formatos, 2. SQL e PowerShell são formatados manualmente; migrations existentes não são reescritas pelo formatador. A configuração está em `.prettierrc.json` e as convenções futuras em `AGENTS.md`.

O histórico local está em [CHANGELOG_DEV.md](CHANGELOG_DEV.md). Para registrar uma etapa em PowerShell 7 (requer `rg`):

```powershell
./scripts/track-step.ps1 -Action Begin -Step step-04
# Implementar, formatar e validar a etapa.
./scripts/track-step.ps1 -Action Finish -Step step-04 -Message 'feat: descreva a mudança' -Validation 'Descreva os testes executados e seus resultados.'
```

O script compara hashes e registra arquivos adicionados, modificados e removidos, sem copiar o conteúdo dos arquivos. Segredos, builds e dependências são excluídos. As mensagens de commit são sugestões; o script não executa commits. O histórico também pode incluir alterações feitas manualmente no workspace durante a etapa.

## API implementada

| Método | Caminho                             | Uso                                              |
| ------ | ----------------------------------- | ------------------------------------------------ |
| GET    | `/health`                           | Disponibilidade do processo                      |
| GET    | `/api/auth/csrf`                    | Token e nome do header CSRF                      |
| POST   | `/api/auth/register/student`        | Cadastro JSON de estudante                       |
| POST   | `/api/auth/register/mentor`         | Cadastro JSON de mentor pendente                 |
| POST   | `/api/auth/login`                   | Form URL-encoded: `email`, `password`            |
| POST   | `/api/auth/logout`                  | Encerrar sessão                                  |
| GET    | `/api/auth/me`                      | Dados da própria conta autenticada               |
| GET    | `/api/admin/mentors/pending?page=1` | Mentores pendentes, 20 por página, somente ADMIN |

Primeiro obtenha `/api/auth/csrf`, preserve o cookie de sessão e envie o token no header retornado em toda escrita, inclusive cadastro e login. Obtenha um novo token após login/logout. Login responde 204 ou 401, sem redirecionamentos. A sessão é renovada no login para impedir fixation. Nenhuma rota pública permite criar administradores.

Cadastro de estudante: `name`, `email`, `password` (12–128 caracteres), `schoolYear` (1–3), `interests`. Cadastro de mentor: `name`, `email`, `password`, `bio`, `expertiseAreas`, `availability`, `experience`. Campos extras são rejeitados. Perfis e credenciais persistem em tabelas separadas; respostas usam DTOs sem hashes.

## Arquitetura e próximos passos

Monólito organizado em `api` (HTTP), `application` (casos de uso/contratos), `domain` (entidades) e `infrastructure` (JPA/segurança). A camada de aplicação acessa persistência por `UserStore`. Anotações JPA no domínio são uma escolha de simplicidade para o MVP.

Esta entrega implementa a fundação e os fluxos de backend das fases 2 e 3: perfis, revisão de mentores, bloqueio de usuários, auditoria e solicitação/aceite/acompanhamento/conclusão de mentorias. Os endpoints, regras de transição e exemplos estão em [docs/mentorias.md](docs/mentorias.md).

Decisões: matching por termos completos normalizados; aceite transacional com bloqueios e índice único; alteração de perfil do mentor exige nova aprovação; resumo estruturado sem chat; bootstrap administrativo explícito, sem servidor HTTP e somente em banco sem usuários. Listas têm páginas de 20 registros. Não foram introduzidas novas dependências nesta etapa.

As fases 4 e 5 agora têm backend de trilhas, módulos, atividades, tentativas, feedback, pontos, níveis, conquistas e progresso. Há seed opcional com nove exercícios e desafio final. Veja [docs/aprendizagem.md](docs/aprendizagem.md) para endpoints, regras e ativação do conteúdo de demonstração.

O MVP ainda não está completo. Pendentes: frontend/layout e dashboards, denúncias e ferramentas completas de moderação, OpenAPI, CI, E2E e validação em PostgreSQL real. Consentimentos institucionais também estão pendentes. Os testes de backend não validam acessibilidade ou experiência de uso do frontend.
