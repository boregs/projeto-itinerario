# Itinerário — frontend demonstrativo

Aplicação independente em Next.js, React e TypeScript para validar a experiência de aprendizagem antes da integração com o backend.

## Executar

Requer Node.js compatível com Next.js 16.

```sh
npm install
npm run dev
```

Acesse `http://localhost:3000`. A demonstração não exige backend, banco de dados ou senha.

## GitHub Pages

O projeto é exportado como site estático e publicado pelo workflow `.github/workflows/nextjs.yml` em:

`https://boregs.github.io/projeto-itinerario/`

Nas configurações do repositório, selecione **GitHub Actions** como fonte em **Settings → Pages**. O workflow valida lint, tipos, testes unitários, exportação estática e jornadas Playwright antes de publicar `frontend/out`.

Para gerar e conferir localmente a mesma versão servida pelo GitHub Pages:

```sh
npm run build:pages
npm run preview:pages
```

Acesse `http://localhost:3000/projeto-itinerario/`. O prefixo é aplicado somente à exportação para Pages; `npm run dev` continua disponível em `http://localhost:3000`.

## Conteúdo atual

- landing page e acesso fictício como estudante;
- dashboard, catálogo, módulos, atividades, progresso e conquistas;
- trilhas de Lógica, Matemática, História, Química e Biologia;
- feedback de resposta, repetição, pontos, nível e conquistas;
- persistência local versionada e restauração dos dados de demonstração;
- layouts mobile-first e navegação acessível.
- acesso demonstrativo separado para estudante, mentor aprovado, mentor pendente e administrador;
- solicitações de mentoria compartilhadas entre os papéis;
- aceite, agendamento, início, conclusão e histórico de mentorias;
- revisão de mentores, gerenciamento de usuários, supervisão e denúncias administrativas.

Os dados são fictícios e ficam somente no navegador. O modo demonstrativo não representa autenticação, autorização ou persistência segura de produção.

Ainda estão pendentes a edição simulada de conteúdo educacional pelo administrador, o formulário de denúncia pelos participantes e a integração com a API Java.

## Validar

```sh
npm run lint
npm run typecheck
npm test
npm run build
npm run build:pages
npx playwright install chromium webkit
npm run test:e2e
npm run test:e2e:pages
```

O Playwright executa a jornada crítica em telefone de 320 pixels, Android/Chromium, iPhone/WebKit, tablet e desktop. Esses testes usam emulação de navegador; não substituem validação em aparelhos físicos.
