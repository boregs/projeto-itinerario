# Histórico de desenvolvimento

Rastreamento iniciado em 17/10/2026. As etapas anteriores estão resumidas no README; não há reconstrução de diffs históricos.

As mensagens abaixo são sugestões de commit, não commits executados. Arquivos locais com segredos, dependências, builds e snapshots não entram nas listas. Este documento é atualizado em todas as etapas.

## Etapa step-01 — 2026-10-17

Commit sugerido: `chore: configure prettier e rastreamento de etapas com ambiente local seguro`

Validação: npm run format e npm run format:check aprovados. Java, XML, JSON, YAML e Markdown formatados; SQL preservado.

| Situação   | Arquivo                                                                           |
| ---------- | --------------------------------------------------------------------------------- |
| Adicionado | `.editorconfig`                                                                   |
| Modificado | `.env.example`                                                                    |
| Modificado | `.gitignore`                                                                      |
| Adicionado | `.prettierignore`                                                                 |
| Adicionado | `.prettierrc.json`                                                                |
| Adicionado | `AGENTS.md`                                                                       |
| Modificado | `docs/mentorias.md`                                                               |
| Adicionado | `package-lock.json`                                                               |
| Adicionado | `package.json`                                                                    |
| Modificado | `pom.xml`                                                                         |
| Modificado | `README.md`                                                                       |
| Adicionado | `scripts/track-step.ps1`                                                          |
| Modificado | `SDD_plataforma_mentoria_aprendizagem.md`                                         |
| Modificado | `src/main/java/br/org/itinerario/api/AdminController.java`                        |
| Modificado | `src/main/java/br/org/itinerario/api/ApiErrors.java`                              |
| Modificado | `src/main/java/br/org/itinerario/api/AuthController.java`                         |
| Modificado | `src/main/java/br/org/itinerario/api/MentorController.java`                       |
| Modificado | `src/main/java/br/org/itinerario/api/MentorshipController.java`                   |
| Modificado | `src/main/java/br/org/itinerario/api/StudentProfileController.java`               |
| Modificado | `src/main/java/br/org/itinerario/application/AccountAccess.java`                  |
| Modificado | `src/main/java/br/org/itinerario/application/BusinessException.java`              |
| Modificado | `src/main/java/br/org/itinerario/application/Matching.java`                       |
| Modificado | `src/main/java/br/org/itinerario/application/MentorService.java`                  |
| Modificado | `src/main/java/br/org/itinerario/application/MentorshipService.java`              |
| Modificado | `src/main/java/br/org/itinerario/application/RegistrationService.java`            |
| Modificado | `src/main/java/br/org/itinerario/application/SafeText.java`                       |
| Modificado | `src/main/java/br/org/itinerario/application/SafeTextValidator.java`              |
| Modificado | `src/main/java/br/org/itinerario/application/UserStore.java`                      |
| Modificado | `src/main/java/br/org/itinerario/domain/AuditEvent.java`                          |
| Modificado | `src/main/java/br/org/itinerario/domain/MentorProfile.java`                       |
| Modificado | `src/main/java/br/org/itinerario/domain/Mentorship.java`                          |
| Modificado | `src/main/java/br/org/itinerario/domain/MentorshipRequest.java`                   |
| Modificado | `src/main/java/br/org/itinerario/domain/StudentProfile.java`                      |
| Modificado | `src/main/java/br/org/itinerario/domain/User.java`                                |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/ActiveUserFilter.java`            |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/AdminBootstrap.java`              |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/AuditRepository.java`             |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/AuthRateLimitFilter.java`         |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/JpaUserStore.java`                |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/MentorshipRepository.java`        |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/MentorshipRequestRepository.java` |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/SecurityConfig.java`              |
| Modificado | `src/main/java/br/org/itinerario/infrastructure/UserRepository.java`              |
| Modificado | `src/main/java/br/org/itinerario/ItinerarioApplication.java`                      |
| Modificado | `src/main/resources/application.yml`                                              |
| Modificado | `src/test/java/br/org/itinerario/AdminBootstrapTest.java`                         |
| Modificado | `src/test/java/br/org/itinerario/AuthIntegrationTest.java`                        |
| Modificado | `src/test/java/br/org/itinerario/MatchingTest.java`                               |
| Modificado | `src/test/java/br/org/itinerario/MentorshipIntegrationTest.java`                  |
| Atualizado | `CHANGELOG_DEV.md`                                                                |

## Etapa step-02 — 2026-10-17

Commit sugerido: `feat: adicione trilhas atividades progresso e conquistas com pontuacao segura`

Validação: Maven verify aprovado: 21 testes sem falhas, incluindo concorrência de pontuação. Três migrations aplicadas e validadas em H2; PostgreSQL real pendente. Prettier aprovado.

| Situação   | Arquivo                                                                        |
| ---------- | ------------------------------------------------------------------------------ |
| Modificado | `.gitignore`                                                                   |
| Adicionado | `src/main/java/br/org/itinerario/api/LearningController.java`                  |
| Adicionado | `src/main/java/br/org/itinerario/application/LearningContentService.java`      |
| Adicionado | `src/main/java/br/org/itinerario/application/LearningProgressService.java`     |
| Adicionado | `src/main/java/br/org/itinerario/domain/Activity.java`                         |
| Adicionado | `src/main/java/br/org/itinerario/domain/LearningModule.java`                   |
| Adicionado | `src/main/java/br/org/itinerario/domain/LearningStatus.java`                   |
| Adicionado | `src/main/java/br/org/itinerario/domain/LearningTrack.java`                    |
| Adicionado | `src/main/java/br/org/itinerario/domain/StudentAchievement.java`               |
| Adicionado | `src/main/java/br/org/itinerario/domain/StudentActivityAttempt.java`           |
| Adicionado | `src/main/java/br/org/itinerario/domain/StudentActivityCompletion.java`        |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/AchievementRepository.java`    |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/ActivityRepository.java`       |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/AttemptRepository.java`        |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/CompletionRepository.java`     |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/DemoLearningSeed.java`         |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/LearningModuleRepository.java` |
| Adicionado | `src/main/java/br/org/itinerario/infrastructure/LearningTrackRepository.java`  |
| Adicionado | `src/main/resources/db/migration/V3__learning_and_progress.sql`                |
| Adicionado | `src/test/java/br/org/itinerario/LearningIntegrationTest.java`                 |
| Atualizado | `CHANGELOG_DEV.md`                                                             |

## Etapa step-03 — 2026-10-17

Commit sugerido: `docs: documente aprendizagem configuracao local e fluxo de contribuicao`

Validação: Prettier aprovado. Documentação alinhada aos endpoints, às três migrations e ao build com 21 testes aprovados. Nenhuma nova alteração de lógica nesta etapa.

| Situação   | Arquivo                |
| ---------- | ---------------------- |
| Modificado | `.env.example`         |
| Modificado | `database/README.md`   |
| Adicionado | `docs/aprendizagem.md` |
| Modificado | `README.md`            |
| Atualizado | `CHANGELOG_DEV.md`     |

## Etapa step-04 — 2026-10-17

Commit sugerido: `docs: adicione schema PostgreSQL consolidado para inspeção e criação manual`

Validação: Script SQL revisado contra as migrations V1-V3; documentação atualizada. Prettier check continua aprovado para arquivos suportados.

| Situação   | Arquivo            |
| ---------- | ------------------ |
| Adicionado | `database/02_schema.sql` |
| Modificado | `database/README.md` |
| Atualizado | `CHANGELOG_DEV.md` |
