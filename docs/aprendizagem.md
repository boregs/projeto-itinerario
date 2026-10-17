# Aprendizagem, progresso e conquistas

Backend das fases 4 e 5 do SDD. O conteúdo pode ser administrado pela API; a interface Next.js ainda está pendente. Todas as rotas exigem autenticação e as escritas exigem token CSRF.

## API do estudante

| Método | Caminho                        | Resultado                                                      |
| ------ | ------------------------------ | -------------------------------------------------------------- |
| GET    | `/api/tracks?page=1`           | Até 20 trilhas ativas                                          |
| GET    | `/api/tracks/{id}`             | Trilha e módulos ativos, na ordem definida                     |
| GET    | `/api/modules/{id}`            | Conteúdo e atividades ativas, sem gabarito                     |
| GET    | `/api/activities/{id}`         | Questão e alternativas, sem gabarito                           |
| POST   | `/api/activities/{id}/attempt` | Registra tentativa e retorna feedback                          |
| GET    | `/api/progress/me?page=1`      | Pontos totais, nível e progresso das trilhas da página         |
| GET    | `/api/achievements/me`         | Conquistas do próprio estudante                                |
| GET    | `/api/attempts/me?page=1`      | Até 20 tentativas próprias, da mais recente para a mais antiga |

Tentativa (índice da alternativa começando em zero):

```json
{ "answer": 1 }
```

O feedback contém `attemptId`, `correct`, `correctAnswer`, `explanation`, `awardedPoints` e `totalPoints`. O cliente não pode enviar pontos, aprovação ou identificador de outro estudante. Respostas inválidas são rejeitadas antes de gravar uma tentativa. Cada resposta válida é registrada, inclusive erros e repetições.

## Regras de progresso

- Uma atividade é concluída no primeiro acerto. Somente esse acerto concede pontos; repetições continuam registradas com zero pontos.
- Transação, bloqueio por estudante e unicidade de conclusão por estudante/atividade impedem pontuação duplicada em requisições simultâneas.
- Um módulo é concluído quando todas as suas atividades ativas foram acertadas. Módulos sem atividades ativas não são considerados concluídos.
- Progresso da trilha = módulos concluídos / módulos ativos, arredondado para baixo em percentual inteiro. Trilha sem módulos ativos permanece em 0%.
- Os pontos acumulados são históricos e permanecem quando conteúdo é inativado. O percentual reflete o conteúdo ativo atual, podendo mudar se a administração adicionar ou inativar conteúdo.
- Nível = `1 + pontos / 100`, usando divisão inteira. Não há ranking público.
- `FIRST_ACTIVITY`: primeiro acerto. `FIRST_TRACK`: primeiro acerto que completa uma trilha. Conquistas são persistentes e únicas por estudante, sem revogação retroativa ao editar conteúdo.
- Conteúdo inativo, incluindo qualquer ancestral inativo, retorna 404 ao estudante. Tentativas antigas permanecem no histórico, sem revelar novamente o texto do conteúdo inativo.

`StudentProgress` é uma projeção calculada pelo servidor a partir das conclusões, sem uma tabela redundante. O catálogo inicial das duas conquistas usa códigos estáveis no domínio; `student_achievement` guarda as concessões. Essas decisões evitam divergência entre tabelas de progresso e tentativas.

## Administração

| Método     | Caminho                               | Operação                                  |
| ---------- | ------------------------------------- | ----------------------------------------- |
| GET/POST   | `/api/admin/tracks`                   | Listar trilhas (com paginação) / criar    |
| PUT/DELETE | `/api/admin/tracks/{id}`              | Editar / inativar preservando histórico   |
| GET        | `/api/admin/tracks/{id}/modulos`      | Listar módulos da trilha                  |
| POST       | `/api/admin/modules`                  | Criar módulo                              |
| PUT        | `/api/admin/modules/{id}`             | Editar módulo                             |
| GET        | `/api/admin/activities?moduleId={id}` | Listar atividades do módulo, com gabarito |
| POST       | `/api/admin/activities`               | Criar atividade                           |
| PUT        | `/api/admin/activities/{id}`          | Editar atividade                          |

Criação de trilha:

```json
{
  "title": "Introdução à Lógica e Algoritmos",
  "description": "Uma trilha para começar a organizar ideias.",
  "status": "ACTIVE",
  "position": 0
}
```

Criação de módulo: `trackId`, `title`, `content`, `status`, `position`. O conteúdo é texto simples. Criação de atividade:

```json
{
  "moduleId": "UUID_DO_MODULO",
  "title": "Primeiros passos",
  "type": "MULTIPLE_CHOICE",
  "question": "Qual é o resultado de 2 + 3?",
  "options": ["4", "5", "6"],
  "correctAnswer": 1,
  "explanation": "Somando duas unidades a três, obtemos cinco.",
  "points": 10,
  "status": "ACTIVE",
  "position": 0
}
```

Tipos: `MULTIPLE_CHOICE`, `TRUE_FALSE`, `LOGIC_CHALLENGE`. Verdadeiro/falso exige exatamente `['Verdadeiro', 'Falso']` nessa ordem. O desafio lógico também usa alternativas neste MVP. Cada atividade aceita 2–6 alternativas distintas e 0–100 pontos. `position` deve ser não negativo; empates são ordenados pelo UUID.

Após a primeira tentativa, pergunta, alternativas, tipo, gabarito e pontos da atividade ficam protegidos contra alteração. Para mudar a avaliação, crie outra atividade. Título, explicação, status e ordem continuam editáveis. Módulos e atividades não podem mudar de trilha/módulo; isso preserva o significado do histórico. A administração registra criação, edição e arquivamento em `audit_event`.

## Conteúdo de demonstração

Habilite explicitamente ao iniciar:

```powershell
java -jar target/itinerario-api-0.1.0-SNAPSHOT.jar --app.demo-content=true
```

Cria a trilha obrigatória **Introdução à Lógica e Algoritmos** com 3 módulos de 3 atividades e um módulo adicional para o desafio final (10 atividades, até 100 pontos). A carga usa IDs estáveis, roda em transação e não duplica nem sobrescreve uma trilha de demonstração existente. Não cria usuários nem senhas.

## Banco e validação

A migration `V3__learning_and_progress.sql` acrescenta `learning_track`, `learning_module`, `activity`, `student_activity_attempt`, `student_activity_completion` e `student_achievement`. As migrations anteriores não foram alteradas.

Testes cobrem feedback, gabarito ausente antes da tentativa, pontuação única, concorrência, conquistas, isolamento entre estudantes, conteúdo inativo, proteção de avaliações respondidas e carga de demonstração repetida. Execução validada em H2 em modo PostgreSQL; PostgreSQL real e testes de interface permanecem pendentes.
