# LearningMore

## 📌 Overview

**LearningMore** is a secure academic platform designed to manage courses, class sessions, educational resources, assignments, submissions, and communication between students and professors.

This project is developed in the context of **DESOFS (Dependable and Secure Software Systems)** and follows a **Secure Software Development Lifecycle (SSDLC)** approach, with a strong focus on security analysis, threat modeling, and secure design.

---

## 🎯 Objectives

The main goal of LearningMore is to provide a backend system that:

* Manages academic entities such as users, courses, and submissions
* Enforces strict access control based on user roles
* Ensures secure handling of sensitive data and files
* Implements security best practices from design to deployment

---

## 👥 Roles

The system supports three main roles:

* **Admin**

    * Manages users and system configuration

* **Professor**

    * Creates courses and assignments
    * Uploads materials
    * Reviews submissions

* **Student**

    * Enrolls in courses
    * Accesses materials
    * Submits assignments
    * Participates in course discussions

---

## 🧠 Domain Model (High-Level)

Main aggregates:

* **User**
* **Course**
* **Submission**
* **Chat**

Supporting entities:

* Enrollment
* ClassSession
* Resource
* Assignment
* ChatMessage

---

## 🏗️ Architecture

The project follows a layered architecture:

```
api            → REST controllers  
application    → use cases / business logic orchestration  
domain         → core domain model  
infrastructure → persistence, external systems  
config         → configuration (security, etc.)
```

---

## ⚙️ Tech Stack

* Java
* Spring Boot
* Maven
* Spring Web
* Spring Security
* Spring Data JPA (planned)
* PostgreSQL (planned)
* Flyway (planned)

---

## 📁 Project Structure

```
src/
 └── main/
     ├── java/com/grupo/learningmore/
     │   ├── api/
     │   ├── application/
     │   ├── domain/
     │   ├── infrastructure/
     │   └── config/
     │
     └── resources/

docs/
Deliverables/
```

---

## 🚀 Getting Started

### Run the application

```bash
  .\mvnw spring-boot:run
```

### Test endpoint

```
GET http://localhost:8080/api/health
```

Response:

```json
{
  "status": "ok"
}
```

---

## 🔐 Security Focus

This project is developed with security as a primary concern.

Key areas:

* Authentication and authorization (RBAC)
* Secure file handling (uploads/downloads)
* Input validation and sanitization
* Protection against common attacks (e.g., injection, path traversal)
* Logging and monitoring
* Secure dependency management

---

## 📘 Phase 1 Plan

### Goal

Define the system from an analysis and security perspective, including:

* Requirements
* Domain model
* Data flows
* Threat modeling
* Security controls
* Test planning

---

### Scope

* System overview
* Functional requirements
* Non-functional requirements
* Security requirements
* Domain model
* Data Flow Diagrams (DFD Level 0 and 1)
* Abuse cases
* STRIDE threat modeling
* Risk assessment
* Mitigation strategies
* Secure architecture and design
* Security testing plan
* ASVS checklist

---

### Deliverables

Located in:

```
Deliverables/Phase1/
```

Includes:

* Phase1-Deliverable.md
* ASVS-Phase1.md

---

### Documentation

Located in:

```
docs/
```

Structure:

```
docs/
 ├── requirements/
 ├── domain-model/
 ├── diagrams/
 ├── abuse-cases/
 ├── threat-model/
 ├── architecture/
 └── testing/
```

---

## ⚠️ Security Requirements (High-Level)

* Passwords stored using secure hashing
* Role-based access control enforced on all endpoints
* File access restricted to authorized users only
* Server-side validation for all inputs
* Protection against:

    * SQL Injection
    * Path Traversal
    * Broken Access Control
* Logging of critical actions

---

## 🔍 Abuse Cases (Examples)

* Unauthorized access to other students' submissions
* Attempt to download restricted files
* Brute force login attempts
* Upload of malicious files
* Privilege escalation attempts
* Access to solutions before allowed time

---

## 🧪 Security Testing Plan (Preview)

* Authentication testing
* Authorization testing
* Input validation testing
* File handling security testing
* Abuse case validation
* Threat mitigation verification

---

## 🔄 Development Workflow

### Branching

* `main` → stable
* `develop` → integration
* `feature/*` → new features

### Example:

```
feature/domain-model
feature/auth-security
feature/submissions
```

---

### Commits

Use clear messages:

```
feat: add user entity
fix: correct authorization logic
docs: add threat model draft
refactor: restructure domain layer
```

---

## 📌 Current Status

* Project initialized
* Spring Boot configured
* Basic domain structure created
* Health endpoint implemented

---

## 📅 Timeline

* Phase 1: Analysis & Design
* Phase 2: Implementation & Validation

---

## 👨‍💻 Team


| Name                | Number        | Contact             |
|---------------------|---------------|---------------------|
| Ana Guterres        | 1221933       | 1221933@isep.ipp.pt |
| Bruno Jesus         | 1221944       | 1221944@isep.ipp.pt |
| Fábio Carido        | 1250512       | 1250512@isep.ipp.pt |
| Guilherme Rodrigues | 1211474       | 1211474@isep.ipp.pt |

---

## 📄 License

Academic project for DESOFS.
