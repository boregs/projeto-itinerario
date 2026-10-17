# Mentores e mentorias — API

Todas as rotas exigem autenticação. Escritas exigem CSRF. Listagens recebem `page` (a partir de 1) e retornam até 20 registros. Nenhuma resposta entre participantes contém e-mail ou hash de senha. Textos são dados simples; o futuro frontend deve renderizá-los como texto, nunca HTML.

## Perfis e administração

| Método  | Caminho                           | Permissão / comportamento                             |
| ------- | --------------------------------- | ----------------------------------------------------- |
| GET/PUT | `/api/student/profile`            | Perfil do próprio estudante                           |
| GET/PUT | `/api/mentor/profile`             | Perfil do próprio mentor; edição exige nova aprovação |
| GET     | `/api/mentors`                    | Lista apenas mentores ativos e aprovados              |
| GET     | `/api/mentors/{id}`               | Perfil aprovado, sem contato pessoal                  |
| GET     | `/api/admin/mentors/pending`      | Administrador: perfis pendentes                       |
| POST    | `/api/admin/mentors/{id}/approve` | Administrador: aprovar perfil pendente                |
| POST    | `/api/admin/mentors/{id}/reject`  | Administrador: rejeitar perfil pendente               |
| GET     | `/api/admin/users`                | Administrador: listar usuários                        |
| PUT     | `/api/admin/users/{id}/status`    | Administrador: `{"status":"BLOCKED"}` ou `ACTIVE`     |
| GET     | `/api/admin/mentorships`          | Administrador: listar mentorias para supervisão       |

Perfil do estudante: `name`, `schoolYear` (1–3), `interests`. Edição do mentor: `name`, `bio`, `expertiseAreas`, `availability`, `experience`. Uma edição do mentor retorna o perfil a `PENDING_APPROVAL`; novas mentorias e avanços de sessão ficam suspensos até revisão. O histórico permanece acessível. Contas ADMIN não podem ser bloqueadas por esta operação.

## Solicitação e matching

Estudante cria via `POST /api/mentorship-requests`:

```json
{
  "subject": "Entender condicionais",
  "area": "Lógica",
  "description": "Preciso de exemplos de decisões em algoritmos.",
  "objective": "Resolver exercícios de se e senão.",
  "preferredSchedule": "Tarde"
}
```

`GET /api/mentorship-requests/me` retorna somente solicitações do estudante autenticado. `POST /api/mentorship-requests/{id}/cancel` cancela uma solicitação própria ainda `OPEN`.

Mentores aprovados usam `GET /api/mentorship-requests/available`. Matching exige área e disponibilidade compatíveis; campos do mentor aceitam listas separadas por vírgula, ponto e vírgula ou barra (ex.: `Matemática / Lógica` e `Manhã; Tarde`). A comparação ignora acentos, caixa e espaços repetidos, mas exige termos completos; não há inferência de sinônimos. A solicitação informa **uma** área e **uma** faixa. A data exata é registrada no aceite; a faixa livre não é um calendário e não verifica automaticamente o horário da data escolhida.

Aceite por mentor aprovado: `POST /api/mentorship-requests/{id}/accept`:

```json
{ "scheduledAt": "2030-01-15T17:00:00Z" }
```

Substitua por uma data futura com offset ou UTC. O servidor verifica aprovação, bloqueio dos participantes, matching e disponibilidade da solicitação na transação. Bloqueio de linha e unicidade de `request_id` impedem criar duas mentorias para a mesma solicitação. Pedidos de estudantes bloqueados não aparecem na lista disponível.

## Acompanhamento e conclusão

- `GET /api/mentorships/me`: sessões do estudante ou mentor autenticado.
- `GET /api/mentorships/{id}`: participantes ou administrador; outros recebem 404.
- `PUT /api/mentorships/{id}/status`: alterações permitidas conforme abaixo.

```json
{ "status": "IN_PROGRESS" }
```

```json
{ "status": "COMPLETED", "summary": "Praticamos condicionais e resolvemos dois exercícios." }
```

Transições: `MATCHED → IN_PROGRESS → COMPLETED`. `MATCHED` e `IN_PROGRESS` também podem ir para `CANCELLED`. Estados finais não reabrem. Mentor aprovado responsável ou administrador inicia/conclui; estudante pode apenas cancelar sua sessão, sem editar resumo. Conclusão exige resumo e registra `completedAt`. O estado da solicitação é atualizado na mesma transação.

## Segurança e limites desta etapa

Auditoria registra ator, alvo, ação e instante para revisão de mentores, mudanças de perfil, bloqueios e eventos de mentoria; não grava senhas, tokens ou textos privados. Campos compartilhados recusam padrões óbvios de telefone, e-mail, links e redes sociais. O filtro é preventivo e não substitui moderação humana; textos ofuscados ainda precisam de revisão. Não há chat nem comentários livres.

Testes automatizados usam H2 em modo PostgreSQL e incluem aceite concorrente, isolamento de acesso, matching, bloqueio e transições. A validação em PostgreSQL real e a interface Next.js ainda estão pendentes.
