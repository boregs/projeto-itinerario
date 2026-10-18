# Itinerário — frontend demonstrativo

Aplicação independente em Next.js, React e TypeScript para validar a experiência de aprendizagem antes da integração com o backend.

## Executar

Requer Node.js compatível com Next.js 16.

```sh
npm install
npm run dev
```

Acesse `http://localhost:3000`. A demonstração não exige backend, banco de dados ou senha.

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
npx playwright install chromium webkit
npm run test:e2e
```

O Playwright executa a jornada crítica em telefone de 320 pixels, Android/Chromium, iPhone/WebKit, tablet e desktop. Esses testes usam emulação de navegador; não substituem validação em aparelhos físicos.
