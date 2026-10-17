# Convenções de desenvolvimento

- Use Java 17 e mantenha o escopo definido no SDD.
- Formate código e documentos com `npm run format` e valide com `npm run format:check`. O Prettier usa plugins de Java e XML; SQL e PowerShell devem ser formatados manualmente.
- Em cada etapa, registre em `CHANGELOG_DEV.md` os arquivos adicionados, modificados ou removidos, uma mensagem de commit sugerida e os resultados de validação. Não confunda mensagem sugerida com commit executado.
- Use `scripts/track-step.ps1` para iniciar e fechar snapshots das etapas. Snapshots ficam em `.local/tracking`, fora do versionamento.
- Nunca coloque senhas em `.env.example`, documentos, logs ou commits. `.env` é configuração local ignorada; não imprima seu conteúdo.
- Não altere migrations já aplicadas: adicione novas versões Flyway.
- Teste regras críticas no backend antes de declarar uma funcionalidade concluída. Documente limitações de validação em PostgreSQL real e da interface ainda pendente.
