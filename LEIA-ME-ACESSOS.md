# PetWeb — áreas por perfil

## Acessos

- Administrador (ROLE_ADMIN): /admin/home, clientes, veterinários, produtos e relatório geral de tutores e animais (/animais/consulta).
- Usuário (ROLE_USER): /home ou /usuario/home, animais, consultas e criação de pedidos.
- Cada perfil é bloqueado ao acessar diretamente URLs da outra área, inclusive em requisições POST. O login redireciona para o painel correspondente.
- Cadastro público sempre cria ROLE_USER e ignora ID e perfil enviados pelo formulário.

## Conta administradora

Cadastre uma conta normalmente. No PostgreSQL configurado no projeto, promova somente a conta escolhida com este comando, substituindo o login:

```sql
UPDATE usuario SET role = 'ROLE_ADMIN' WHERE login_usuario = 'login_do_administrador';
```

Saia e faça login novamente após mudar o perfil. Usuários comuns devem ter role = 'ROLE_USER'. Não foi criada uma senha padrão de administrador.

## Executar e verificar

Requer JDK 17 ou superior e PostgreSQL configurado em src/main/resources/application.properties.

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd '-Dtest=AccessControlTests,RegistrationSecurityTests' test
```

Os testes de acesso usam contas temporárias em memória, sem conexão com o banco. Verificam login, páginas de cada perfil, bloqueios GET/POST, logout e proteção do cadastro público. O teste original PetwebApplicationTests depende do ambiente da aplicação.

Esta alteração separa permissões por perfil. Os módulos existentes continuam usando os registros globais do sistema; não foi implementada uma vinculação dos registros a cada conta individual.
