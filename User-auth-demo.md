# User/Auth Security Demo

## 1. Arrancar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação deve ficar disponível em:

```text
http://localhost:9393
```

---

## 2. Criar utilizador

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        name="Alex"
        email="alex@test.com"
        password="Password123"
    } | ConvertTo-Json)
```

Resultado esperado:

* utilizador criado;
* role atribuída automaticamente como `STUDENT`;
* password não é devolvida na resposta.

---

## 3. Fazer login

```powershell
$login = Invoke-RestMethod `
    -Uri "http://localhost:9393/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        email="alex@test.com"
        password="Password123"
    } | ConvertTo-Json)

$login
```

Resultado esperado:

* JWT token devolvido;
* dados básicos do utilizador;
* role do utilizador.

---

## 4. Guardar o Bearer Token

```powershell
$token = $login.token
$token.Length
```

Resultado esperado:

* token JWT guardado;
* tamanho do token superior a 0.

---

## 5. Testar endpoint protegido sem token

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me" `
    -Method GET
```

Resultado esperado:

* acesso negado;
* o endpoint exige autenticação.

---

## 6. Testar endpoint protegido com Bearer token

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
```

Resultado esperado:

* dados do utilizador autenticado;
* confirmação de JWT Bearer funcional.

---

## 7. Testar login falhado

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        email="alex@test.com"
        password="WrongPassword123"
    } | ConvertTo-Json)
```

Resultado esperado:

* erro de autenticação;
* tentativa falhada registada nos logs.

---

## 8. Testar rate limiting

Repetir várias vezes o login falhado:

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        email="alex@test.com"
        password="WrongPassword123"
    } | ConvertTo-Json)
```

Resultado esperado:

* após várias tentativas falhadas, o sistema bloqueia temporariamente o login;
* devolve erro `429 Too Many Requests`.

---

## 9. Alterar password

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me/password" `
    -Method PUT `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body (@{
        currentPassword="Password123"
        newPassword="NewPassword123"
    } | ConvertTo-Json)
```

Resultado esperado:

* password alterada com sucesso;
* token antigo fica invalidado por alteração do `tokenVersion`.

---

## 10. Confirmar invalidação do token antigo

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
```

Resultado esperado:

* acesso negado;
* o token antigo deixa de ser aceite.

---

## 11. Login com nova password

```powershell
$login = Invoke-RestMethod `
    -Uri "http://localhost:9393/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        email="alex@test.com"
        password="NewPassword123"
    } | ConvertTo-Json)

$token = $login.token
```

Resultado esperado:

* login feito com a nova password;
* novo JWT emitido.

---

## 12. Resumo técnico

Funcionalidades demonstradas:

* registo seguro de utilizador;
* role pública limitada a `STUDENT`;
* password hashing com BCrypt;
* login real;
* JWT Bearer authentication;
* endpoints protegidos;
* RBAC;
* rate limiting de login;
* alteração segura de password;
* invalidação de tokens antigos com `tokenVersion`;
* logs de eventos de autenticação.
