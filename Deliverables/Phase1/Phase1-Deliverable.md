# Phase 1 – Analysis / Requirements & Design

## 1. Project Description

LearningMore is a secure academic platform designed to support course management, class materials distribution, assignment submissions, and communication between students and professors.

The system is developed in the context of DESOFS and follows a Secure Software Development Lifecycle (SSDLC), focusing on secure design, threat modeling, and risk mitigation.

---

## 2. System Overview

### Actors

* Admin
* Professor
* Student

### Core Components

* REST API
* Database (relational)
* File storage system

### Assets

* User credentials
* Course materials
* Assignment submissions
* Grades and feedback
* Chat messages
* Logs

### System Boundary

The system includes the backend API, database, and file storage. External entities interact through HTTP requests.

---

## 3. Functional Requirements

### User Management

* FR1: Admin can create users
* FR2: Admin can assign roles
* FR3: Users can authenticate using email and password

### Course Management

* FR4: Professors can create courses
* FR5: Professors can manage course information
* FR6: Students can enroll in courses
* FR7: Users can view enrolled courses

### Resource Management

* FR8: Professors can upload class materials
* FR9: Students can access course materials

### Assignment Management

* FR10: Professors can create assignments
* FR11: Assignments must have deadlines

### Submission Management

* FR12: Students can submit assignments
* FR13: Professors can view submissions
* FR14: Professors can grade submissions

### Communication

* FR15: Users can send messages in course chat
* FR16: Users can read course messages

### Logging

* FR17: System logs authentication events
* FR18: System logs critical actions

---

## 4. Non-Functional Requirements

* NFR1: The system must be implemented as a REST API
* NFR2: The system must use a relational database
* NFR3: The system must support concurrent users
* NFR4: The system must ensure data consistency
* NFR5: The system must support logging and monitoring
* NFR6: The system must be modular and maintainable
* NFR7: The system must support automated testing

---

## 5. Security Requirements

### 5.1 Authentication and Access Control

* SR1: Passwords must be securely hashed
* SR2: The system must enforce RBAC (Admin, Professor, Student)
* SR3: Access must be validated per request
* SR4: Users can only access resources they are authorized for

### 5.2 Data Security

* SR5: Files must be stored outside public directories
* SR6: File access must be restricted
* SR7: Sensitive data must not be exposed

### 5.3 Communication Security

* SR8: HTTPS must be enforced in production
* SR9: Secure headers must be implemented

### 5.4 Input Validation

* SR10: All inputs must be validated server-side
* SR11: File uploads must be validated (type, size)
* SR12: Path traversal must be prevented

### 5.5 Dependency Security

* SR13: Dependencies must be monitored for vulnerabilities

### 5.6 Logging and Monitoring

* SR14: Security-relevant events must be logged
* SR15: Logs must not contain sensitive data

---

## 6. Abuse Cases

* AC1: Unauthorized access to submissions
* AC2: Unauthorized file download
* AC3: Brute force login
* AC4: Malicious file upload
* AC5: Path traversal
* AC6: Privilege escalation
* AC7: Access to solutions before release
* AC8: Chat spam abuse
* AC9: Unauthorized course access
* AC10: Submission timestamp manipulation

---

## 7. General Design

The system follows a layered architecture:

* API layer (controllers)
* Application layer (use cases)
* Domain layer (business logic)
* Infrastructure layer (database, storage)

---

## 8. Domain Model

Main aggregates:

* User
* Course
* Submission
* Chat

Supporting entities:

* Enrollment
* Assignment
* Resource
* ChatMessage

---

## 9. Data Flow Diagrams

### 9.1 DFD Level 0

(To be added)

### 9.2 DFD Level 1

(To be added)

---

## 10. Threat Modeling

Threat modeling will be performed using the STRIDE methodology applied to DFD elements.

---

## 11. Risk Assessment

Risks will be evaluated based on:

* Likelihood
* Impact
* Severity

---

## 12. Mitigations

Mitigation strategies will be defined for each identified threat.

---

## 13. Secure Design

* Enforce server-side authorization
* Validate all inputs
* Use secure file storage
* Apply least privilege principle
* Deny access by default

---

## 14. Secure Architecture

The architecture separates concerns between layers and enforces:

* controlled access to resources
* secure communication
* isolation of sensitive components

---

## 15. Security Test Planning

The system will include:

* authentication tests
* authorization tests
* input validation tests
* file handling tests
* abuse case validation

---

## 16. Traceability Matrix

Each:

* security requirement
* threat
* mitigation
* test

will be mapped for traceability.

---

## 17. References

* Domain Model (PlantUML)
* Abuse Cases document
* Requirements documents
* Repository structure
