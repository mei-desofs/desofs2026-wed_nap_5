# Phase 2 – Sprint 2: Development, Testing & Deployment

<!-- TOC -->
* [Phase 2 – Sprint 2: Development, Testing & Deployment](#phase-2--sprint-2-development-testing--deployment)
  * [1. Overview](#1-overview)
  * [2. Sprint 2 Scope](#2-sprint-2-scope)
  * [3. Development](#3-development)
  * [4. Security Improvements](#4-security-improvements)
  * [5. Build and Test](#5-build-and-test)
  * [6. CI/CD Pipeline](#6-cicd-pipeline)
  * [7. Runtime Security Testing](#7-runtime-security-testing)
  * [8. Production and Operate Evidence](#8-production-and-operate-evidence)
  * [9. ASVS Traceability](#9-asvs-traceability)
  * [10. Demonstration Guide](#10-demonstration-guide)
  * [11. Conclusion](#11-conclusion)
<!-- TOC -->

---

## 1. Overview

This deliverable documents the work completed during **Phase 2 – Sprint 2** for the LearningMore project.

The main focus of this sprint was to consolidate the secure backend implementation, improve the authentication and authorization flows, increase automated testing coverage, add runtime security validation, and strengthen DevSecOps evidence for deployment readiness.

LearningMore is a secure academic platform that supports:

- user management;
- authentication and authorization;
- course management;
- resource management;
- assignment and submission management;
- course chat rooms;
- audit logging and security monitoring.

---

## 2. Sprint 2 Scope

The Sprint 2 work focused on the following areas:

| Area | Work performed |
|---|---|
| Development | User/Auth hardening, endpoint fixes, chat validation fixes, ID generation changes, bootstrap/Postman support |
| Authentication | JWT Bearer authentication, password hashing, login attempt protection |
| Authorization | RBAC with ADMIN, PROFESSOR and STUDENT roles |
| Session Security | JWT token versioning and invalidation after password changes/account deactivation |
| Logging | Authentication logs, user lifecycle logs, exception logs and chat operation logs |
| Testing | Unit tests, integration tests, runtime API tests and Postman/Newman collection |
| Pipeline | Unified CI/CD pipeline with build, test, SAST, SCA, DAST, mutation testing and runtime checks |
| ASVS | ASVS tracker updated according to the implemented security controls |

---

## 3. Development

### 3.1 Backend Aggregates

The project maintains a Domain-Driven Design structure with multiple aggregates:

| Aggregate | Responsibility |
|---|---|
| User | Identity, authentication data, roles, active status and token version |
| Course | Course creation, update, deletion and enrollment |
| Assignment | Assignment lifecycle, deadlines and course association |
| Submission | Student submissions, professor grading and feedback |
| Resource | Course material upload and retrieval |
| Chat | Chat rooms and course messages |

### 3.2 Main REST Controllers

The following REST controllers are available in the backend:

| Controller | Main responsibility |
|---|---|
| `AuthController` | Login and JWT issuing |
| `UserController` | User registration, profile, password change and account deactivation |
| `CourseController` | Course management and enrollment |
| `AssignmentController` | Assignment management |
| `SubmissionController` | Submission and grading operations |
| `ResourceController` | Resource upload and access |
| `ChatController` | Chat rooms and messages |
| `HealthController` | Health endpoint used by pipeline/runtime tests |

### 3.3 User and Authentication Improvements

Sprint 2 introduced or consolidated the following security-related user features:

- public user registration creates only `STUDENT` accounts;
- passwords are encoded with `BCryptPasswordEncoder`;
- authenticated users can retrieve their own profile using `GET /api/users/me`;
- authenticated users can change their password using `PUT /api/users/me/password`;
- administrators can deactivate users using `PUT /api/users/{id}/deactivate`;
- deactivated users can no longer authenticate successfully;
- password changes and account deactivation increment `tokenVersion`, invalidating previously issued JWTs.

### 3.4 ID Generation Changes

The project was updated to improve the way identifiers are generated and represented across entities and endpoints.

This helped standardize API behavior, simplify Postman/Newman testing, and reduce mismatch issues between API input, authentication identity and persistence identifiers.

---

## 4. Security Improvements

### 4.1 Authentication

Authentication is implemented using JWT Bearer tokens.

When a user logs in successfully:

1. the backend validates the email and password;
2. the password is checked using BCrypt;
3. the system validates that the account is active;
4. failed login counters are reset;
5. a JWT is issued with the user identifier, role and token version.

Implemented controls:

- secure password hashing;
- JWT signature validation;
- token expiration validation;
- login attempt tracking;
- temporary blocking after excessive failed attempts;
- generic unauthorized responses for invalid credentials.

### 4.2 Authorization and RBAC

The application uses Spring Security with role-based access control.

Current roles:

- `ADMIN`;
- `PROFESSOR`;
- `STUDENT`.

Security configuration follows a deny-by-default approach:

- `/api/auth/**` is public;
- `POST /api/users` is public for student registration;
- `/api/admin/**` requires `ADMIN`;
- `/api/professor/**` requires `PROFESSOR`;
- `/api/student/**` requires `STUDENT`;
- all other endpoints require authentication.

Additional endpoint-level authorization is applied through annotations such as:

```java
@PreAuthorize("hasRole('ADMIN')")
```

### 4.3 Session and Token Invalidation

The user entity includes a `tokenVersion` field.

The JWT includes a matching `tokenVersion` claim. During request filtering, the backend compares the token version against the current value stored in the database.

A token becomes invalid when:

- the user changes password;
- the account is deactivated;
- the token is expired;
- the user no longer exists;
- the token version does not match the database value.

This provides stateless session invalidation without storing issued tokens server-side.

### 4.4 CSRF Configuration

CSRF protection is disabled for the stateless REST API:

```java
.csrf(AbstractHttpConfigurer::disable)
```

This is aligned with the use of JWT Bearer tokens and avoids CSRF token requirements for API clients such as Postman/Newman.

### 4.5 Logging and Traceability

Logging was improved across several components.

| Component | Logged events |
|---|---|
| `AuthController` | login attempts, successful login, failed login, blocked login, inactive account login |
| `UserController` | user creation, profile retrieval, password change request, account deactivation |
| `UserService` | user creation, password changes, failed password validation, user deactivation |
| `GlobalExceptionHandler` | access denied, validation errors, invalid arguments, unexpected errors |
| `ChatService` | message sending activity |

Sensitive values such as raw passwords and JWT tokens are not logged.

---

## 5. Build and Test

The project currently includes automated tests for service and API behavior.

The test suite includes:

| Test category | Examples |
|---|---|
| Unit tests | UserService, JwtService, AssignmentService, CourseService, ResourceService, SubmissionService |
| Integration tests | CourseController, ResourceController, AssignmentController, SubmissionController, ChatController |
| Security-oriented tests | login behavior, token issuing, token validation, password change, token versioning |
| Runtime API tests | Postman collection executed with Newman |

The latest local execution result was:

```text
Tests run: 132
Failures: 0
Errors: 0
BUILD SUCCESS
```

---

## 6. CI/CD Pipeline

A unified GitHub Actions pipeline is used for automated build, quality checks and security testing.

The workflow is configured for:

- `main`;
- `phase2-sprint*`;
- pull requests targeting those branches;
- manual workflow dispatch.

Pipeline jobs include:

| Job | Purpose |
|---|---|
| Secret Detection | Detect committed secrets using Gitleaks |
| Build & Test | Compile project, run tests and generate SBOM |
| Code Quality | Run Checkstyle and SpotBugs |
| SAST | Run CodeQL static analysis |
| SCA | Run OWASP Dependency-Check |
| Trivy | Filesystem vulnerability and misconfiguration scan |
| PIT Mutation | Mutation testing |
| Runtime API & Security Tests | Start application, run Newman and ZAP baseline |
| Release | Release automation for main branch |

### 6.1 Software Composition Analysis

OWASP Dependency-Check is used to identify vulnerable dependencies.

The workflow disables unauthenticated OSS Index analysis to avoid external `401 Unauthorized` failures and relies on NVD/CISA-based analysis.

The pipeline still produces Dependency-Check reports as artifacts for vulnerability management and review.

### 6.2 SBOM

CycloneDX is configured to generate a Software Bill of Materials during the build process.

This supports dependency inventory and supply-chain security review.

---

## 7. Runtime Security Testing

Runtime testing was added using:

- Spring Boot application startup in CI;
- health check readiness verification;
- Newman execution of the Sprint 2 Postman collection;
- OWASP ZAP baseline scan.

The Postman collection validates the application API flows and supports demonstration of:

- user creation;
- login;
- token usage;
- protected endpoints;
- course operations;
- assignment/submission flows;
- chat/resource flows.

---

## 8. Production and Operate Evidence

Although production deployment is not the main focus of the project, Sprint 2 added evidence for production and operational readiness.

### 8.1 Production

| Practice | Evidence |
|---|---|
| Configuration management | Environment-specific test configuration and application properties |
| Logging and traceability | Authentication, user lifecycle, exception and chat logs |
| Patch management | Dependency scanning through OWASP Dependency-Check and Trivy |
| Release management | Release workflow available for main branch |
| Security configuration | Stateless JWT security, RBAC and deny-by-default access control |

### 8.2 Operate

| Practice | Evidence |
|---|---|
| Monitoring readiness | `/api/health` endpoint used in runtime pipeline |
| Vulnerability management | SCA and Trivy reports generated in CI |
| Penetration testing support | OWASP ZAP baseline scan |
| Runtime validation | Newman API collection |
| Incident investigation support | Structured logs for authentication, authorization failures and unexpected errors |

---

## 9. ASVS Traceability

The ASVS tracker was updated during Sprint 2 to reflect the implemented security controls.

Main affected ASVS areas:

| ASVS area | Sprint 2 evidence |
|---|---|
| Authentication | BCrypt password hashing, login endpoint, failed login handling |
| Session Management | JWT expiration and token version invalidation |
| Access Control | RBAC and protected endpoints |
| Token Security | Signed JWTs, token claims and backend validation |
| Input Validation | DTO validation with Jakarta Validation |
| Logging | authentication/user lifecycle/exception logging |
| Error Handling | global exception handler with generic responses |
| API Security | CSRF disabled for stateless JWT API, protected endpoints |
| Software Supply Chain | SCA, SBOM and dependency scanning |

---

## 10. Demonstration Guide

### 10.1 Start the application

```powershell
.\mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:9393
```

### 10.2 Create a student account

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

Expected result:

- a new user is created;
- role is automatically `STUDENT`;
- password is not returned.

### 10.3 Login

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

Expected result:

- JWT token is returned;
- user details and role are returned.

### 10.4 Store Bearer token

```powershell
$token = $login.token
$token.Length
```

### 10.5 Access protected profile endpoint

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

- authenticated user profile is returned.

### 10.6 Access protected endpoint without token

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me" `
    -Method GET
```

Expected result:

- request is denied.

### 10.7 Change password

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

Expected result:

- password is changed;
- old JWT becomes invalid.

### 10.8 Confirm old token invalidation

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:9393/api/users/me" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
```

Expected result:

- request is denied because the old token version no longer matches.

### 10.9 Login with the new password

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

Expected result:

- login succeeds;
- a new JWT token is issued.

---

## 11. Conclusion

Sprint 2 consolidated the secure implementation of LearningMore.

The project now includes:

- multiple DDD aggregates;
- REST API functionality across users, courses, resources, assignments, submissions and chat;
- role-based authorization;
- JWT authentication;
- password hashing;
- token invalidation;
- login rate limiting;
- secure user lifecycle controls;
- runtime API validation;
- automated DevSecOps pipeline;
- SAST, SCA, DAST, mutation testing and runtime testing;
- logging and traceability evidence;
- ASVS tracker updates.

This sprint improved both the functional maturity and the security posture of the system, aligning the implementation with the SSDLC objectives defined for the project.
