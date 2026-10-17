# Preparação do PostgreSQL

1. Use PostgreSQL 17 e conecte ao banco `postgres` com uma conta administradora.
2. Execute os dois comandos de `01_create_database.sql` **separadamente, com autocommit**. Se a role ou o banco já existirem, não repita o comando correspondente.
3. Defina uma senha para a role `itinerario` nas propriedades do pgAdmin ou usando `\password itinerario` no psql. Não salve a senha em scripts versionados.
4. Configure a aplicação pelo `.env` na raiz (leitura automática ao iniciar nessa pasta) ou por variáveis de ambiente no PowerShell:

```powershell
$env:DATABASE_URL = 'jdbc:postgresql://localhost:5432/itinerario'
$env:DATABASE_USER = 'itinerario'
$env:DATABASE_PASSWORD = '<senha definida por você>'
$env:COOKIE_SECURE = 'false' # somente desenvolvimento com HTTP local
mvn spring-boot:run
```

As tabelas serão criadas automaticamente pelo **Flyway**, usando os SQLs em `src/main/resources/db/migration`. Não execute esses SQLs manualmente no banco da aplicação: isso criaria tabelas sem o histórico do Flyway. Não habilite baseline automático para contornar conflitos de schema. Se já criou tabelas manualmente, use um banco vazio para esta primeira instalação e preserve os dados existentes.

O script provisiona banco e proprietário para desenvolvimento. Não precisa instalar extensões. Hibernate valida o schema, sem criar ou alterar tabelas. O frontend usará a API e não precisa de acesso ao banco.

## Tabelas desta etapa

- `V1__identity.sql`: `app_user`, `student_profile`, `mentor_profile`.
- `V2__mentorship_and_audit.sql`: `mentorship_request`, `mentorship`, `audit_event`, índices e chaves estrangeiras.
- `V3__learning_and_progress.sql`: trilhas, módulos, atividades, tentativas, conclusões e conquistas. Aplicada automaticamente ao iniciar a versão atual.
- `flyway_schema_history`: criada e mantida pelo Flyway.

Os scripts de tabelas estão em `src/main/resources/db/migration`, que é a fonte única de schema. As próximas funcionalidades acrescentarão novas migrations; não altere versões já aplicadas.

Para uma visão consolidada de todas as tabelas atuais, consulte [`02_schema.sql`](02_schema.sql). Ele reúne as migrations V1–V3 em uma transação para uso manual em banco vazio. Não o execute junto com o Flyway nem em um banco já migrado.

## Primeiro administrador

Antes de cadastrar qualquer usuário, com o banco sem usuários e a aplicação parada, execute uma única instância do bootstrap. Ele cria o administrador com hash PBKDF2 e encerra sem abrir servidor HTTP. Não promove contas existentes, não atualiza senhas e recusa banco com usuários.

```powershell
mvn package
$env:BOOTSTRAP_ADMIN_EMAIL = 'admin@demo.local'
$bootstrapSecret = Read-Host 'Senha inicial (12 a 128 caracteres)' -AsSecureString
$env:BOOTSTRAP_ADMIN_PASSWORD = [System.Net.NetworkCredential]::new('', $bootstrapSecret).Password
try {
    java -jar target/itinerario-api-0.1.0-SNAPSHOT.jar --spring.main.web-application-type=none --app.bootstrap-admin=true
} finally {
    Remove-Item Env:BOOTSTRAP_ADMIN_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:BOOTSTRAP_ADMIN_EMAIL -ErrorAction SilentlyContinue
    $bootstrapSecret.Dispose()
}
```

Depois inicie normalmente com `mvn spring-boot:run`. As credenciais de conexão PostgreSQL precisam estar configuradas nos dois comandos. Para bancos que já possuem usuários, a recuperação/provisão administrativa deve ser feita por procedimento separado e revisado; não apague usuários para executar o bootstrap.
