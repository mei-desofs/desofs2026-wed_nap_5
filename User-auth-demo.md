# User/Auth Security Demo

## 1. Arrancar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação fica disponível em:

```text
http://localhost:9393
```

---

## 2. Criar utilizador

```powershell
$email = "demo$(Get-Random)@test.com"

$user = Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        name="Demo User"
        email=$email
        password="Password123"
        role="STUDENT"
    } | ConvertTo-Json)

$user
$email
```

Resultado esperado:

* utilizador criado;
* ID gerado no formato `USR-...`;
* role `STUDENT`;
* password não é devolvida na resposta.

---

## 3. Fazer login

```powershell
$login = Invoke-RestMethod `
    -Uri "http://localhost:9393/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        email=$email
        password="Password123"
    } | ConvertTo-Json)

$login
```

Resultado esperado:

* JWT devolvido;
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

## 5. Testar endpoint protegido com Bearer token

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

## 6. Testar endpoint protegido sem token

```powershell
try {
    Invoke-RestMethod `
        -Uri "http://localhost:9393/api/users/me" `
        -Method GET
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

Resultado esperado:

```text
403
```

---

## 7. Testar token inválido

```powershell
try {
    Invoke-RestMethod `
        -Uri "http://localhost:9393/api/users/me" `
        -Method GET `
        -Headers @{ Authorization = "Bearer abc123" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

Resultado esperado:

```text
403
```

---

## 8. Alterar password

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me/password" `
    -Method PUT `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body (@{
        currentPassword="Password123"
        newPassword="NovaPassword123"
    } | ConvertTo-Json)
```

Resultado esperado:

* password alterada com sucesso;
* resposta sem erro;
* token antigo deixa de ser válido devido ao incremento do `tokenVersion`.

---

## 9. Login com password antiga

```powershell
try {
    Invoke-RestMethod `
        -Uri "http://localhost:9393/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body (@{
            email=$email
            password="Password123"
        } | ConvertTo-Json)
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

Resultado esperado:

```text
401
```

---

## 10. Login com nova password

```powershell
$login = Invoke-RestMethod `
    -Uri "http://localhost:9393/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        email=$email
        password="NovaPassword123"
    } | ConvertTo-Json)

$token = $login.token
$login
```

Resultado esperado:

* login feito com a nova password;
* novo JWT emitido.

---

## 11. Demonstração em Postman

### Register User

```http
POST http://localhost:9393/api/users
```

Body → raw → JSON:

```json
{
  "name": "Demo User",
  "email": "demo@test.com",
  "password": "Password123",
  "role": "STUDENT"
}
```

### Login

```http
POST http://localhost:9393/api/auth/login
```

Body → raw → JSON:

```json
{
  "email": "demo@test.com",
  "password": "Password123"
}
```

### Get Profile

```http
GET http://localhost:9393/api/users/me
```

Authorization:

```text
Bearer Token
```

Colar apenas o token, sem aspas e sem escrever `Bearer`.

---

## 12. Resumo técnico

Funcionalidades demonstradas:

* registo de utilizador;
* IDs customizados no formato `USR-...`;
* password hashing com BCrypt;
* login real;
* JWT Bearer authentication;
* endpoints protegidos;
* RBAC;
* alteração segura de password;
* invalidação de tokens antigos com `tokenVersion`;
* logs de eventos de autenticação e ações de utilizador.
