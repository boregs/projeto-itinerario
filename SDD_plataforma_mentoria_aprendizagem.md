# SDD — Plataforma de Mentoria Acadêmica e Aprendizagem Interativa

> **Uso deste documento:** este arquivo deve servir como instrução principal para um agente de IA responsável por projetar e implementar o MVP.  
> O agente deve tratar este documento como a fonte de verdade do escopo.  
> Não adicionar funcionalidades fora do escopo sem solicitação explícita.

---

## 1. Visão do produto

Desenvolver uma **plataforma web educacional** voltada a estudantes do Ensino Médio, com dois eixos principais:

1. **Mentoria acadêmica:** conectar estudantes que buscam orientação a mentores/voluntários previamente aprovados.
2. **Aprendizagem interativa:** oferecer trilhas, desafios, quizzes e atividades lúdicas para apoiar o estudo de conteúdos escolares, começando por **lógica e conceitos introdutórios de algoritmos**.

O sistema deve ser simples, acessível, seguro e adequado ao contexto escolar.

---

## 2. Problema que o produto busca enfrentar

A comunidade escolar apresenta dificuldades relacionadas à falta de orientação acadêmica e ao acompanhamento contínuo dos estudantes.

Entre os problemas observados estão:

- pouca oferta de suporte individualizado;
- dificuldade para organizar os estudos;
- dúvidas sobre conteúdos escolares;
- insegurança sobre escolhas acadêmicas;
- falta de acompanhamento contínuo;
- desmotivação diante de conteúdos considerados difíceis;
- uso de recursos digitais de forma dispersa, sem um ambiente central de apoio.

A solução não deve tentar substituir professores, pedagogos ou serviços de orientação escolar.  
O objetivo é **complementar o apoio existente** por meio de tecnologia.

---

## 3. Objetivo do MVP

Criar um MVP funcional que permita:

- cadastro e autenticação de usuários;
- diferenciação entre estudante, mentor e administrador;
- solicitação de mentoria por estudantes;
- aprovação e gerenciamento de mentores;
- criação e acompanhamento de mentorias;
- acesso a trilhas educacionais;
- resolução de desafios e quizzes;
- acompanhamento do progresso do estudante;
- gamificação simples;
- moderação e administração básica da plataforma.

O MVP deve priorizar **clareza, segurança, acessibilidade e baixo acoplamento**.

---

# 4. Público-alvo

## 4.1 Estudantes

Principalmente estudantes do Ensino Médio.

Necessidades principais:

- tirar dúvidas;
- organizar estudos;
- receber orientação acadêmica;
- receber apoio em conteúdos escolares;
- acompanhar sua evolução;
- estudar por meio de experiências mais interativas.

## 4.2 Mentores

Voluntários aprovados pela administração da plataforma.

Podem ser:

- professores;
- universitários;
- ex-alunos;
- profissionais convidados;
- voluntários da comunidade.

Sua função é orientar os estudantes dentro dos limites definidos pela plataforma.

## 4.3 Administradores

Responsáveis por:

- validar mentores;
- moderar usuários;
- gerenciar conteúdos;
- acompanhar denúncias;
- revisar mentorias;
- manter a segurança do ambiente.

---

# 5. Princípios do produto

O agente de IA deve considerar estes princípios em todas as decisões:

1. **Segurança de menores em primeiro lugar.**
2. **Coleta mínima de dados pessoais.**
3. **Acessibilidade desde o início.**
4. **Interface simples e responsiva.**
5. **Gamificação voltada à aprendizagem, não à competição.**
6. **Mentoria como apoio, não como substituição da escola.**
7. **MVP pequeno e evolutivo.**
8. **Arquitetura simples antes de arquitetura complexa.**
9. **Evitar dependência desnecessária de serviços pagos.**
10. **Preferir soluções open source sempre que possível.**

---

# 6. Features essenciais do MVP

## FE-01 — Cadastro e autenticação

O sistema deve permitir cadastro e login.

Papéis:

- `STUDENT`
- `MENTOR`
- `ADMIN`

### Estudante

Campos mínimos:

- nome de exibição;
- e-mail;
- senha;
- série escolar;
- áreas de interesse.

Evitar coleta de dados sensíveis que não sejam necessários.

### Mentor

Campos mínimos:

- nome;
- e-mail;
- senha;
- breve apresentação;
- áreas de conhecimento;
- disponibilidade;
- vínculo ou experiência relevante.

Todo mentor deve iniciar com status:

`PENDING_APPROVAL`

Somente administradores podem aprovar mentores.

---

## FE-02 — Perfil do estudante

O estudante deve poder visualizar e editar:

- nome de exibição;
- série escolar;
- interesses;
- matérias de maior interesse;
- progresso nas trilhas;
- mentorias atuais e anteriores.

---

## FE-03 — Perfil do mentor

O mentor deve possuir perfil contendo:

- nome;
- apresentação;
- áreas de conhecimento;
- disponibilidade;
- status de aprovação;
- quantidade de mentorias realizadas.

Dados de contato pessoais não devem ser exibidos.

---

## FE-04 — Solicitação de mentoria

O estudante deve poder criar uma solicitação contendo:

- tema;
- área/matéria;
- breve descrição da dificuldade;
- objetivo esperado;
- preferência de horário.

Estados possíveis:

- `OPEN`
- `MATCHED`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

---

## FE-05 — Matching de mentoria

O MVP deve utilizar matching simples por:

1. área de conhecimento;
2. disponibilidade;
3. status aprovado do mentor.

Não implementar algoritmo complexo de recomendação no MVP.

O sistema pode:

- sugerir mentores compatíveis;
- permitir que o administrador associe mentor e estudante;
- permitir que o mentor aceite uma solicitação compatível.

---

## FE-06 — Sessões de mentoria

Uma mentoria deve registrar:

- estudante;
- mentor;
- tema;
- data prevista;
- status;
- notas/resumo da sessão;
- data de criação;
- data de conclusão.

### Regra de segurança

No MVP, a plataforma **não deve possuir chat privado em tempo real entre estudante e mentor**.

A comunicação deve ocorrer por:

- mensagens estruturadas relacionadas à mentoria;
- comentários moderáveis;
- campos limitados de orientação.

Não expor telefone, WhatsApp, endereço, redes sociais ou outros dados pessoais.

---

## FE-07 — Trilhas de aprendizagem

A plataforma deve possuir trilhas educacionais.

A primeira trilha obrigatória será:

**Introdução à Lógica e Algoritmos**

Exemplo de módulos:

1. O que é um algoritmo?
2. Sequência de instruções.
3. Variáveis.
4. Condicionais.
5. Repetições.
6. Introdução a pseudocódigo.
7. Desafio final.

A estrutura deve permitir adicionar futuramente outros conteúdos escolares.

---

## FE-08 — Atividades interativas

Cada módulo pode conter:

- conteúdo textual curto;
- exemplo;
- questão de múltipla escolha;
- verdadeiro/falso;
- desafio lógico;
- feedback imediato.

O sistema deve informar:

- resposta correta;
- explicação;
- resultado da tentativa.

---

## FE-09 — Gamificação

O MVP deve possuir gamificação simples.

Permitido:

- pontos;
- progresso percentual;
- níveis;
- conquistas;
- sequência de atividades concluídas.

Evitar:

- ranking público global;
- exposição de estudantes com menor desempenho;
- mecânicas que estimulem competição excessiva.

A gamificação deve reforçar o progresso individual.

---

## FE-10 — Dashboard do estudante

Deve apresentar:

- progresso geral;
- trilhas em andamento;
- atividades recentes;
- pontos;
- conquistas;
- mentorias abertas;
- próximas mentorias;
- recomendações simples de continuidade.

---

## FE-11 — Dashboard do mentor

Deve apresentar:

- solicitações compatíveis;
- mentorias aceitas;
- próximas sessões;
- mentorias concluídas;
- histórico básico.

---

## FE-12 — Painel administrativo

O administrador deve poder:

- listar usuários;
- visualizar mentores pendentes;
- aprovar ou rejeitar mentores;
- bloquear usuários;
- gerenciar trilhas;
- gerenciar módulos;
- gerenciar atividades;
- visualizar mentorias;
- visualizar denúncias.

---

## FE-13 — Denúncia e moderação

Estudantes e mentores devem poder denunciar:

- comportamento inadequado;
- conteúdo impróprio;
- uso indevido da plataforma.

Uma denúncia deve possuir:

- autor;
- usuário ou conteúdo denunciado;
- motivo;
- descrição;
- status.

Estados:

- `OPEN`
- `UNDER_REVIEW`
- `RESOLVED`
- `DISMISSED`

---

# 7. Requisitos funcionais

## RF-01

O sistema deve permitir cadastro de estudantes.

## RF-02

O sistema deve permitir cadastro de mentores.

## RF-03

O sistema deve impedir que mentores não aprovados realizem mentorias.

## RF-04

O administrador deve poder aprovar e rejeitar mentores.

## RF-05

O estudante deve poder solicitar uma mentoria.

## RF-06

O sistema deve permitir associar uma solicitação a um mentor aprovado.

## RF-07

O estudante deve poder acompanhar o status da solicitação.

## RF-08

O mentor deve poder visualizar mentorias associadas a ele.

## RF-09

O sistema deve permitir concluir uma mentoria.

## RF-10

O sistema deve disponibilizar trilhas educacionais.

## RF-11

O estudante deve poder iniciar e concluir atividades.

## RF-12

O sistema deve registrar o progresso do estudante.

## RF-13

O sistema deve fornecer feedback após atividades.

## RF-14

O sistema deve registrar pontos e conquistas.

## RF-15

O administrador deve poder criar e editar conteúdos educacionais.

## RF-16

O sistema deve permitir denúncias.

## RF-17

O administrador deve poder revisar denúncias.

## RF-18

O sistema deve aplicar autorização de acesso de acordo com o papel do usuário.

---

# 8. Requisitos não funcionais

## RNF-01 — Responsividade

A aplicação deve funcionar corretamente em:

- smartphone;
- tablet;
- desktop.

Mobile-first é recomendado.

---

## RNF-02 — Acessibilidade

Seguir, sempre que possível:

**WCAG 2.2 nível AA.**

Obrigatório:

- navegação por teclado;
- contraste adequado;
- labels em formulários;
- textos alternativos;
- estados de foco visíveis;
- HTML semântico;
- mensagens de erro compreensíveis;
- não depender apenas de cor para transmitir informação.

---

## RNF-03 — Segurança

Obrigatório:

- senhas armazenadas somente como hash seguro;
- HTTPS em produção;
- validação de entrada;
- proteção contra SQL Injection;
- proteção contra XSS;
- autorização por roles;
- rate limiting em autenticação;
- tokens/sessões com expiração;
- logs de ações administrativas;
- tratamento seguro de erros.

---

## RNF-04 — Privacidade

Aplicar princípios de minimização de dados.

Considerar a LGPD.

Como a solução pode envolver menores:

- evitar coleta de dados desnecessários;
- não permitir exposição pública de dados pessoais;
- não exibir contato privado entre estudante e mentor;
- permitir exclusão ou anonimização de conta quando aplicável;
- registrar consentimentos exigidos pelo contexto escolar.

A implementação final deve ser validada conforme as regras da instituição responsável.

---

## RNF-05 — Performance

Meta inicial:

- páginas principais carregarem em até aproximadamente 2 segundos em condições normais;
- APIs comuns responderem preferencialmente em menos de 500 ms, desconsiderando serviços externos;
- paginação em listagens administrativas.

---

## RNF-06 — Manutenibilidade

Código deve possuir:

- separação clara entre domínio, aplicação e infraestrutura;
- nomes descritivos;
- validações centralizadas;
- tratamento consistente de erros;
- testes automatizados nas regras críticas.

---

## RNF-07 — Observabilidade

Registrar:

- erros;
- tentativas de login suspeitas;
- alterações administrativas;
- aprovação/rejeição de mentores;
- denúncias;
- eventos relevantes de mentoria.

Nunca registrar senhas, tokens ou dados sensíveis em logs.

---

# 9. Estrutura de páginas

## Públicas

### `/`

Landing page.

Conteúdo:

- propósito da plataforma;
- como funciona a mentoria;
- como funciona a aprendizagem;
- benefícios;
- botão entrar;
- botão criar conta.

### `/login`

Login.

### `/cadastro`

Escolha do tipo de conta.

### `/cadastro/estudante`

Cadastro de estudante.

### `/cadastro/mentor`

Cadastro de mentor.

### `/sobre`

Descrição do projeto.

### `/privacidade`

Política de privacidade.

### `/termos`

Termos de uso.

---

# 10. Área do estudante

### `/aluno/dashboard`

Cards:

- progresso;
- pontos;
- mentorias;
- atividades recentes;
- continuar estudando.

### `/aluno/mentorias`

Lista:

- solicitações abertas;
- mentorias em andamento;
- mentorias concluídas.

### `/aluno/mentorias/nova`

Formulário de solicitação.

### `/aluno/mentorias/{id}`

Detalhes da mentoria.

### `/aluno/trilhas`

Lista de trilhas disponíveis.

### `/aluno/trilhas/{id}`

Detalhes da trilha.

### `/aluno/trilhas/{id}/modulos/{id}`

Conteúdo do módulo.

### `/aluno/atividades/{id}`

Execução da atividade.

### `/aluno/progresso`

Histórico de aprendizagem.

### `/aluno/conquistas`

Pontos e conquistas.

### `/aluno/perfil`

Configurações do perfil.

---

# 11. Área do mentor

### `/mentor/dashboard`

Resumo de atividades.

### `/mentor/solicitacoes`

Solicitações compatíveis.

### `/mentor/mentorias`

Mentorias atuais e anteriores.

### `/mentor/mentorias/{id}`

Detalhes da mentoria.

### `/mentor/perfil`

Perfil e disponibilidade.

---

# 12. Área administrativa

### `/admin/dashboard`

Indicadores gerais.

### `/admin/mentores`

Lista e status dos mentores.

### `/admin/mentores/{id}`

Aprovação/rejeição.

### `/admin/usuarios`

Gerenciamento de usuários.

### `/admin/mentorias`

Visualização das mentorias.

### `/admin/trilhas`

CRUD de trilhas.

### `/admin/trilhas/{id}/modulos`

CRUD de módulos.

### `/admin/atividades`

CRUD de atividades.

### `/admin/denuncias`

Moderação.

---

# 13. Modelo de dados inicial

## User

- id
- name
- email
- passwordHash
- role
- status
- createdAt
- updatedAt

## StudentProfile

- id
- userId
- schoolYear
- interests
- createdAt

## MentorProfile

- id
- userId
- bio
- expertiseAreas
- availability
- approvalStatus
- approvedAt
- approvedBy

## MentorshipRequest

- id
- studentId
- subject
- area
- description
- preferredSchedule
- status
- createdAt

## Mentorship

- id
- requestId
- studentId
- mentorId
- scheduledAt
- status
- summary
- createdAt
- completedAt

## LearningTrack

- id
- title
- description
- status
- order

## LearningModule

- id
- trackId
- title
- content
- order

## Activity

- id
- moduleId
- title
- type
- question
- options
- correctAnswer
- explanation
- points

## StudentActivityAttempt

- id
- studentId
- activityId
- answer
- isCorrect
- score
- createdAt

## StudentProgress

- id
- studentId
- trackId
- completedModules
- percentage
- points

## Achievement

- id
- title
- description
- criteria

## StudentAchievement

- id
- studentId
- achievementId
- achievedAt

## Report

- id
- reporterUserId
- targetUserId
- mentorshipId
- reason
- description
- status
- createdAt
- reviewedAt

---

# 14. Tech stack recomendada

## Frontend

**Next.js + TypeScript**

Bibliotecas sugeridas:

- React;
- Tailwind CSS;
- React Hook Form;
- Zod;
- TanStack Query.

### Motivos

- boa experiência de desenvolvimento;
- componentes reutilizáveis;
- suporte a SSR quando necessário;
- bom ecossistema;
- TypeScript reduz erros;
- adequado para uma aplicação responsiva.

---

## Backend

**Java 17 + Spring Boot 3.5 (Spring MVC)**

Bibliotecas/recursos:

- Spring Data JPA + Hibernate;
- Spring Security com sessões e cookies HTTP-only;
- Jakarta Bean Validation (Hibernate Validator);
- PBKDF2 para senhas, proteção CSRF e limitação de requisições de autenticação;
- OpenAPI via springdoc-openapi (previsto para a documentação da API);
- Flyway para migrations SQL e driver JDBC do PostgreSQL;
- Maven para dependências, compilação e testes.

### Arquitetura recomendada

Usar **monólito modular**.

Não utilizar microserviços no MVP.

Separação sugerida:

```text
src/main/java/br/org/itinerario/
  api/
  application/
  domain/
  infrastructure/
src/main/resources/db/migration/
src/test/java/br/org/itinerario/
```

Evitar abstrações desnecessárias.

---

## Banco de dados

**PostgreSQL**

Motivos:

- open source;
- confiável;
- bom suporte relacional;
- adequado às entidades do projeto.

Usar migrations.

---

## Cache

**Não obrigatório no MVP.**

Redis somente se surgir necessidade real após medição.

---

## Infraestrutura

Desenvolvimento:

- Docker;
- Docker Compose.

Serviços:

- frontend;
- backend;
- PostgreSQL.

Produção pode utilizar:

- frontend em serviço compatível com Next.js;
- API em container ou serviço gerenciado;
- PostgreSQL gerenciado ou container conforme contexto.

A infraestrutura deve permanecer simples.

---

## Controle de versão

Git + GitHub.

Branches sugeridas:

- `main`
- `develop`
- `feature/*`
- `fix/*`

---

## CI

GitHub Actions.

Pipeline mínimo:

1. restore/install;
2. lint;
3. build;
4. testes;
5. geração/verificação de migrations quando aplicável.

---

## Testes

### Backend

- JUnit 5 + Spring Boot Test;
- MockMvc + Spring Security Test;
- Testcontainers com PostgreSQL para testes de integração do banco;
- testes unitários;
- testes de integração para fluxos críticos.

### Frontend

- Vitest;
- React Testing Library;
- Playwright para fluxos E2E essenciais.

---

# 15. API mínima sugerida

## Auth

```http
POST /api/auth/register/student
POST /api/auth/register/mentor
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

## Mentor

```http
GET  /api/mentors
GET  /api/mentors/{id}
PUT  /api/mentor/profile
```

## Mentorias

```http
POST /api/mentorship-requests
GET  /api/mentorship-requests/me
GET  /api/mentorship-requests/available
POST /api/mentorship-requests/{id}/accept
GET  /api/mentorships/me
GET  /api/mentorships/{id}
PUT  /api/mentorships/{id}/status
```

## Aprendizagem

```http
GET  /api/tracks
GET  /api/tracks/{id}
GET  /api/modules/{id}
GET  /api/activities/{id}
POST /api/activities/{id}/attempt
GET  /api/progress/me
GET  /api/achievements/me
```

## Administração

```http
GET  /api/admin/mentors/pending
POST /api/admin/mentors/{id}/approve
POST /api/admin/mentors/{id}/reject

GET  /api/admin/users
PUT  /api/admin/users/{id}/status

POST /api/admin/tracks
PUT  /api/admin/tracks/{id}
DELETE /api/admin/tracks/{id}

POST /api/admin/modules
PUT  /api/admin/modules/{id}

POST /api/admin/activities
PUT  /api/admin/activities/{id}

GET  /api/admin/reports
PUT  /api/admin/reports/{id}
```

---

# 16. Limitações conhecidas do MVP

O MVP não deve tentar resolver toda a jornada educacional.

Limitações previstas:

- dependência da disponibilidade de mentores;
- conteúdo educacional inicial limitado;
- matching simples;
- ausência de comunicação síncrona;
- ausência de aplicação mobile nativa;
- ausência de integrações com sistemas escolares;
- ausência de inteligência artificial personalizada;
- métricas educacionais inicialmente básicas;
- disponibilidade de internet ainda necessária para a maior parte das funcionalidades.

Essas limitações devem ser documentadas e não tratadas como bugs.

---

# 17. Features fora do escopo

Não implementar no MVP:

## FO-01

Aplicativo Android ou iOS nativo.

## FO-02

Videochamadas integradas.

## FO-03

Chat privado em tempo real.

## FO-04

Integração com WhatsApp, Telegram ou redes sociais.

## FO-05

Integração com Google Classroom, Moodle ou sistemas governamentais.

## FO-06

Inteligência artificial generativa para responder automaticamente às dúvidas.

## FO-07

Matching baseado em machine learning.

## FO-08

Marketplace de mentores.

## FO-09

Pagamentos ou mentorias remuneradas.

## FO-10

Ranking público de estudantes.

## FO-11

Fórum público aberto.

## FO-12

Sistema completo de gestão escolar.

## FO-13

Diário de classe.

## FO-14

Controle de frequência escolar.

## FO-15

Boletim oficial.

## FO-16

Correção automática de provas físicas.

## FO-17

Certificados oficiais.

## FO-18

Suporte completo offline.

## FO-19

Notificações push mobile.

## FO-20

Arquitetura de microserviços.

---

# 18. Regras de negócio críticas

## RN-01

Somente mentores aprovados podem aceitar mentorias.

## RN-02

Estudantes não podem alterar o status de aprovação de mentores.

## RN-03

Mentores não podem acessar mentorias de outros mentores.

## RN-04

Estudantes somente podem acessar suas próprias solicitações e mentorias.

## RN-05

Administradores podem visualizar mentorias para fins de segurança e moderação.

## RN-06

Nenhum usuário deve visualizar senha, token ou dados privados de outro usuário.

## RN-07

Dados pessoais de contato não devem ser exibidos entre estudante e mentor.

## RN-08

Cada tentativa de atividade deve ser registrada.

## RN-09

Pontuação somente deve ser concedida conforme regras definidas no backend.

## RN-10

O progresso deve ser calculado pelo servidor.

## RN-11

Usuários bloqueados não podem iniciar novas mentorias.

## RN-12

Conteúdo educacional inativo não deve aparecer para estudantes.

---

# 19. Critérios de aceite do MVP

O MVP será considerado funcional quando:

### Autenticação

- estudante consegue cadastrar-se;
- mentor consegue cadastrar-se;
- usuário consegue autenticar-se;
- permissões são aplicadas corretamente.

### Mentoria

- estudante cria uma solicitação;
- administrador visualiza mentores pendentes;
- administrador aprova mentor;
- mentor aprovado visualiza solicitação compatível;
- mentor aceita a solicitação;
- estudante acompanha o status;
- mentoria pode ser concluída.

### Aprendizagem

- administrador cria uma trilha;
- administrador cria módulos;
- administrador cria atividades;
- estudante acessa a trilha;
- estudante responde atividade;
- sistema fornece feedback;
- progresso é atualizado;
- pontos são registrados.

### Moderação

- usuário cria denúncia;
- administrador visualiza denúncia;
- administrador altera o status da denúncia.

### Qualidade

- aplicação funciona em mobile e desktop;
- fluxos principais possuem testes;
- páginas essenciais são navegáveis por teclado;
- endpoints críticos exigem autenticação/autorização.

---

# 20. Dados iniciais para demonstração

Criar seed de desenvolvimento com:

## Administrador

```text
admin@demo.local
```

Não versionar senha real.

## Mentor

- área: Matemática / Lógica;
- status: aprovado.

## Estudante

- série: 1º ou 2º ano do Ensino Médio.

## Trilha

**Introdução à Lógica e Algoritmos**

Criar ao menos:

- 3 módulos;
- 3 atividades por módulo;
- 1 desafio final.

---

# 21. Diretrizes de UX

A interface deve ser:

- simples;
- amigável;
- objetiva;
- adequada a adolescentes;
- sem excesso de elementos;
- consistente entre páginas.

Evitar visual infantilizado.

### Dashboard

Priorizar:

1. próxima ação;
2. progresso;
3. mentorias;
4. conquistas.

### Formulários

- poucas etapas;
- mensagens de erro claras;
- validação próxima ao campo;
- não apagar dados preenchidos após erro.

---

# 22. Diretrizes para o agente de IA

O agente responsável pela implementação deve seguir estas regras:

1. Não aumentar o escopo sem autorização.
2. Priorizar primeiro as features essenciais.
3. Implementar backend e domínio antes de funcionalidades cosméticas.
4. Não criar microserviços.
5. Não adicionar Redis sem necessidade mensurada.
6. Não adicionar WebSocket/chat no MVP.
7. Não implementar funcionalidades de IA generativa.
8. Não expor dados pessoais de estudantes.
9. Não permitir comunicação privada irrestrita entre menor e mentor.
10. Tratar autorização no backend, nunca apenas no frontend.
11. Criar migrations para mudanças no banco.
12. Criar testes para regras de negócio críticas.
13. Documentar decisões arquiteturais relevantes.
14. Manter o projeto executável localmente via README.
15. Não usar mocks permanentes onde já existe backend funcional.
16. Não armazenar secrets no repositório.
17. Garantir que o projeto compile e os testes passem antes de considerar uma tarefa concluída.

---

# 23. Ordem recomendada de implementação

## Fase 1 — Fundação

- estrutura do projeto;
- banco de dados;
- autenticação;
- roles;
- migrations;
- layout base.

## Fase 2 — Mentores

- cadastro;
- perfil;
- aprovação administrativa.

## Fase 3 — Mentorias

- solicitações;
- matching;
- aceite;
- acompanhamento;
- conclusão.

## Fase 4 — Aprendizagem

- trilhas;
- módulos;
- atividades;
- tentativas;
- feedback;
- progresso.

## Fase 5 — Gamificação

- pontos;
- conquistas;
- indicadores de progresso.

## Fase 6 — Administração e segurança

- moderação;
- denúncias;
- logs;
- revisão de permissões.

## Fase 7 — Qualidade

- acessibilidade;
- testes E2E;
- performance;
- documentação;
- deploy.

---

# 24. Definition of Done

Uma feature somente pode ser considerada concluída quando:

- atende aos requisitos descritos;
- possui validação no backend;
- respeita autorização;
- possui tratamento de erro;
- não introduz exposição indevida de dados;
- funciona em viewport mobile e desktop;
- possui testes adequados à criticidade;
- não quebra testes existentes;
- está documentada quando necessário;
- não adiciona dependências sem justificativa.

---

# 25. Resultado esperado

Ao final do MVP, deve existir uma aplicação web demonstrável na qual:

1. um estudante entra na plataforma;
2. solicita ajuda acadêmica;
3. um mentor aprovado pode aceitar a solicitação;
4. a mentoria é acompanhada pela plataforma;
5. o estudante acessa uma trilha educacional;
6. realiza atividades interativas;
7. recebe feedback;
8. acompanha seu progresso;
9. recebe pontos/conquistas;
10. administradores mantêm controle de mentores, conteúdos e segurança.

O produto deve demonstrar de forma clara a hipótese central da intervenção:

> **Tecnologia pode ampliar o acesso dos estudantes a orientação acadêmica e tornar parte do processo de aprendizagem mais interativa, desde que a solução seja acessível, segura, simples e acompanhada pela comunidade escolar.**
