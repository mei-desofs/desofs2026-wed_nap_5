# Phase 2 – Sprint 1: Development & Testing

<!-- TOC -->
* [Phase 2 – Sprint 1: Development & Testing](#phase-2--sprint-1-development--testing)
  * [1. Overview](#1-overview)
  * [2. Development Practices](#2-development-practices)
    * [2.1 Commit Messages](#21-commit-messages)
    * [2.2 Pull Requests](#22-pull-requests)
    * [2.3 CodeReviews](#23-codereviews)
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

This phase focuses on the integration of secure development and DevSecOps practices into the project lifecycle.

This document presents the development conventions and security practices adopted during the implementation process, as
well as the CI/CD workflows created to support automated validation and secure software delivery.

A shift-left security approach was followed, integrating automated testing and security verification mechanisms 
throughout the development lifecycle.

---

## 2. Development Practices

To ensure consistent code quality and smooth collaboration within the team, a set of development conventions and
processes was defined. These include standards for commit messages, pull request management, code review practices, and
branch organization.

Following these guidelines helps maintain a clean and maintainable codebase, while also improving traceability and ease
of navigation throughout the project.

### 2.1 Commit Messages

The team adopts the Conventional Commits specification to ensure a clear, structured, and consistent commit history
throughout the project. Each commit message follows a standard format that improves readability and helps track the
purpose of changes more effectively.

The general structure used is: '**type**': '**description**'

Where '**description**' should give and explanation of what was done and '**type**' defines the nature of the change:

* **docs**: updates or additions to documentation
* **feat**: introduction of a new feature or functionality
* **fix**: correction of a bug or issue
* **test**: addition or modification of tests
* **chore**: maintenance tasks, dependency updates, and other routine work
* **refactor**: code changes that improve structure without altering behaviour

### 2.2 Pull Requests

Before any changes are merged into the `main` branch, a pull request must be created to ensure proper review and
validation of the implemented work.

**Title:**
Each pull request should have a clear and descriptive title summarizing the changes introduced.

**Description:**
If needed, a description can be provided, including:

* A summary of the changes made
* Any relevant details that help reviewers understand the modifications

**Assignment:**
Pull requests are typically assigned to the developer or developers responsible for the changes, ensuring 
traceability.

**Reviewers:**
Team members are added as reviewers to validate the changes before merging. Automated tools and pipelines may be used 
to support the review process when appropriate.

**Labels:**
When needed labels (e.g., bug, enhancement, documentation) can be used to help organize and categorize pull requests,
depending on their importance.


### 2.3 CodeReviews

Code reviews were used as part of the development workflow to support code quality and encourage knowledge sharing
within the team, although the level of formality varied depending on the task and context.

**Review approach:**
Pull requests were generally reviewed by at least one other team member before being merged, when possible. Feedback and
discussion were used to refine implementations and address potential issues.

**Reviewer focus:**
During review, attention was typically given to aspects such as:

* General correctness of the implementation
* Alignment with the expected functionality
* Results from automated checks and CI/CD pipelines (e.g., workflows, tests, and other validation steps)

**Automated support:**
In some cases, automated tools were used to assist with the review process by providing suggestions and highlighting 
potential issues. These tools were used as a complement to manual review rather than a strict requirement.

**Merge process:**
Once feedback was addressed and the changes were considered appropriate, the pull request could be merged into the main
branch by the author or another team member, depending on the situation.

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