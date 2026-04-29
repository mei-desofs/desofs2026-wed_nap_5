# Phase 2 – Sprint 1: Development & Testing

<!-- TOC -->
* [Phase 2 – Sprint 1: Development & Testing](#phase-2--sprint-1-development--testing)
  * [1. Overview](#1-overview)
  * [2. Development Practices](#2-development-practices)
    * [2.1 Secure Coding Practices](#21-secure-coding-practices)
    * [2.2 Code Reviews](#22-code-reviews)
  * [3. Static Analysis (SAST & SCA)](#3-static-analysis-sast--sca)
    * [3.1 Static Application Security Testing (SAST)](#31-static-application-security-testing-sast)
    * [3.2 Software Composition Analysis (SCA)](#32-software-composition-analysis-sca)
  * [4. Dynamic & Interactive Testing](#4-dynamic--interactive-testing)
    * [4.1 Dynamic Application Security Testing (DAST)](#41-dynamic-application-security-testing-dast)
    * [4.2 Interactive Application Security Testing (IAST)](#42-interactive-application-security-testing-iast)
  * [5. Security Testing](#5-security-testing)
    * [5.1 Testing Strategy](#51-testing-strategy)
    * [5.2 Security Test Cases](#52-security-test-cases)
  * [6. DevSecOps Pipeline (Initial Setup)](#6-devsecops-pipeline-initial-setup)
    * [6.1 Pipeline Overview](#61-pipeline-overview)
    * [6.2 Pipeline Stages](#62-pipeline-stages)
  * [7. Evidence](#7-evidence)
  * [8. Conclusion](#8-conclusion)
<!-- TOC -->

---

## 1. Overview

This sprint focuses on the implementation of secure development practices and the execution of security testing
activities.

The work is based on Phase 1 outputs:

- Threat Modeling (STRIDE)
- Risk Assessment
- Security Requirements
- Abuse Cases

The main objective is to integrate security into development and validate it through testing.

---

## 2. Development Practices

### 2.1 Secure Coding Practices

The following practices were implemented:

- Server-side validation of all inputs
- Role-Based Access Control (RBAC) enforcement
- Secure password storage (e.g., BCrypt / Argon2)
- Secure error handling (no sensitive data exposure)
- File upload validation (type, size, format)

---

### 2.2 Code Reviews

Code reviews were performed using pull requests.

Process:

- All code changes are submitted via Pull Requests
- Reviewers validate:
    - Security issues
    - Code quality
    - Compliance with requirements

Evidence:

- PR links:
    - [PR #1](#)
    - [PR #2](#)

---

## 3. Static Analysis (SAST & SCA)

### 3.1 Static Application Security Testing (SAST)

Static analysis was used to detect vulnerabilities in the source code.

Tools used:

- [Insert tool name]

Results:

- Total issues:
- Critical:
- High:
- Medium:

Mitigation:

- Fixed vulnerabilities:
- Remaining issues:

---

### 3.2 Software Composition Analysis (SCA)

Dependency analysis was performed to identify vulnerable libraries.

Tools used:

- npm audit / OWASP Dependency Check / Snyk

Results:

- Vulnerable dependencies:
- Severity levels:

Mitigation:

- Updated dependencies
- Accepted risks (if applicable)

---

## 4. Dynamic & Interactive Testing

### 4.1 Dynamic Application Security Testing (DAST)

Dynamic testing was performed on the running application.

Tools used:

- OWASP ZAP / Burp Suite

Scope:

- Authentication endpoints
- File management
- Chat system

Results:

- Vulnerabilities found:
- Severity:

Mitigation:

- Fixes applied:

---

### 4.2 Interactive Application Security Testing (IAST)

IAST was used to analyze application behavior during execution.

Tools used:

- [Insert tool name]

Scope:

- Runtime behavior analysis
- Detection of vulnerabilities during execution

Results:

- Issues identified:
- Mitigation actions:

---

## 5. Security Testing

### 5.1 Testing Strategy

The testing approach is:

- Threat-driven (based on STRIDE)
- Abuse-case-driven
- Aligned with OWASP ASVS

---

### 5.2 Security Test Cases

Derived from Phase 1 abuse cases:

| Test ID | Description           | Expected Result        | Status |
|---------|-----------------------|------------------------|--------|
| ST-01   | Unauthorized access   | Access denied          |        |
| ST-02   | Brute force login     | Blocked / rate limited |        |
| ST-03   | Malicious file upload | Rejected               |        |
| ST-04   | Privilege escalation  | Prevented              |        |

---

## 6. DevSecOps Pipeline (Initial Setup)

### 6.1 Pipeline Overview

An initial CI pipeline was created to automate:

- Build process
- Test execution
- Basic security checks

---

### 6.2 Pipeline Stages

1. Install dependencies
2. Build application
3. Run tests
4. Run SCA (dependency analysis)

---

## 7. Evidence

Include:

- Code review screenshots (PRs)
- SAST reports
- SCA results
- DAST scan outputs
- Pipeline execution logs

---

## 8. Conclusion

This sprint demonstrates the integration of security into the development phase through:

- Secure coding practices
- Code reviews
- Static and dynamic analysis
- Initial DevSecOps pipeline setup

---