# Painel administrativo PetWeb

O administrador usa `/admin`, com o mesmo template `home.html` e o mesmo CSS do painel comum. Todos os módulos existentes ficam disponíveis: clientes, animais, veterinários, consultas, produtos, pedidos e consulta geral. O login do administrador passa por `/home` e redireciona para `/admin`.

A rota `/admin` exige `ROLE_ADMIN`. As demais funcionalidades aceitam `ROLE_USER` ou `ROLE_ADMIN`, preservando o acesso atual dos usuários comuns. O cadastro público sempre cria `ROLE_USER` e não aceita atualizar um usuário existente por ID.

## Definir um administrador

Cadastre a conta normalmente e, no PostgreSQL da aplicação, execute a atualização para o login desejado:

```sql
UPDATE usuario
SET role = 'ROLE_ADMIN'
WHERE login_usuario = 'LOGIN_DO_ADMIN';
```

Substitua `LOGIN_DO_ADMIN` pelo login correto e confirme que apenas a conta desejada foi alterada. Saia e faça login novamente para carregar as permissões. Nenhuma conta administrativa ou senha padrão é criada automaticamente.

## Executar e testar

Use um JDK completo 17 ou superior. Configure o PostgreSQL usando `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`. Credenciais não estão incluídas no projeto. O serviço de e-mail existente também precisa de `SPRING_MAIL_HOST` e `SPRING_MAIL_USERNAME`; configure porta e autenticação conforme seu provedor, usando variáveis de ambiente para credenciais.

```sh
bash mvnw test
bash mvnw spring-boot:run
```

No Windows, use `mvnw.cmd test` e `mvnw.cmd spring-boot:run`.

Os testes usam H2 em memória e não acessam o banco real nem enviam e-mails. Verificam autorização, login, redirecionamento, proteção do cadastro, renderização dos módulos e igualdade do CSS entre administrador e usuário comum.

Foram corrigidos também problemas encontrados no arquivo original: configuração duplicada de `PasswordEncoder`, nomes inconsistentes de `TokenRecuperacao`/`TokenRecuperacaoRepository`, consulta de usuário por e-mail ausente, dependência de e-mail ausente e ativação da página personalizada de login.
