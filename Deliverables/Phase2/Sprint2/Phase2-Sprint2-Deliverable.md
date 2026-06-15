# Phase 2 – Sprint 2: Development, Testing & Deployment

<!-- TOC -->
* [Phase 2 – Sprint 2: Development, Testing & Deployment](#phase-2--sprint-2-development-testing--deployment)
<!-- TOC -->

---

## 1. Overview

This sprint focused on extending the DevSecOps practices introduced in the previous sprint by strengthening the security
pipeline, improving testing strategies, and validating deployment and configuration mechanisms.

The objective was to integrate security controls throughout the software development lifecycle, ensuring continuous
verification of code quality, dependency security, runtime behavior, and deployment processes.

The sprint also emphasized secure configuration, automated security assessments, and the adoption of security testing
techniques to improve the overall resilience of the application.

---

## 2. Development Practices

This section describes the development processes and collaboration practices followed during the sprint to ensure code
quality, maintainability, and security.

### 2.1 Code Reviews

Describe:

* Pull request review process
* Number of required reviewers (if applicable)
* Review criteria used
* Security-related aspects verified during reviews
* Use of automated review tools

Possible topics:

* Code correctness
* Secure coding practices
* Compliance with project standards
* Test coverage validation
* Pipeline execution results

Include screenshot:

```text
![Code Review](images/code-review.png)
```

### 2.2 Branching Strategy

Describe the branch organization adopted during Sprint 2.

Possible branches:

* `main`
* `phase2-sprint2`
* `feature/...`
* `hotfix/...` (if used)

Explain:

* Development flow
* Merge strategy
* Release process

---

## 3. DevSecOps Pipeline

### 3.1 Pipeline Overview

Provide an overview of the CI/CD and security workflows implemented during the sprint.

Describe:

* GitHub Actions usage
* Workflow triggers
* Security gates
* Artifact generation
* Release automation

Include a pipeline diagram if available.

```text
![Pipeline Overview](images/pipeline-overview.png)
```

---

### 3.2 Build and Testing Workflow

Describe:

* Application build process
* Unit tests execution
* Integration tests execution
* Test reports generation

Topics:

* Maven lifecycle
* Automated execution
* Failure handling

Include workflow snippet and screenshots.

---

### 3.3 Static Application Security Testing (SAST)

Describe the use of SAST tools such as:

* CodeQL
* SpotBugs Security Plugin
* SonarQube (if applicable)

Discuss:

* Vulnerability detection
* Secure coding verification
* Integration with GitHub Security tab

Include screenshots:

```text
![SAST Results](images/sast-results.png)
```

---

### 3.4 Dynamic Application Security Testing (DAST)

Describe the execution of runtime security analysis using tools such as OWASP ZAP.

Topics:

* Baseline scan
* Authentication testing (if applicable)
* Endpoint discovery
* Vulnerability detection

Examples of findings:

* Missing headers
* Information disclosure
* Input validation issues

Include reports and screenshots.

---

### 3.5 Interactive Application Security Testing (IAST)

Describe whether IAST was implemented.

If implemented:

* Tool used
* Runtime monitoring capabilities
* Vulnerabilities detected during execution

If not implemented:

* Explain limitations or project constraints.

---

### 3.6 Software Composition Analysis (SCA)

Describe dependency analysis.

Possible tools:

* OWASP Dependency Check
* Dependabot
* GitHub Dependency Graph

Topics:

* Vulnerable dependencies detection
* CVE monitoring
* Dependency updates

Include screenshots.

---

### 3.7 Security Configuration and Installation

Describe the security configuration applied to the application and deployment environment.

Possible topics:

* Environment variables
* Secret management
* JWT configuration
* HTTPS configuration
* CORS configuration
* Spring Security configuration
* Docker security settings (if applicable)
* Secure default configurations

Explain how security settings are applied during deployment.

---

### 3.8 Deployment Workflow

Describe the deployment process implemented during the sprint.

Topics:

* Automated releases
* Release Please
* Environment preparation
* Artifact generation
* Deployment targets
* Rollback mechanisms (if any)

Include screenshots.

---

### 3.9 Security Gates and Pipeline Enforcement

Describe security controls that must pass before deployment.

Examples:

* Build success
* Unit tests passing
* SAST passing
* SCA passing
* DAST execution
* Code review approval

Explain whether deployment or release is blocked when security checks fail.

---

## 4. Security Testing

### 4.1 Security Testing Strategy

Describe the overall testing strategy adopted.

Possible testing categories:

* Unit testing
* Integration testing
* Security testing
* Authentication testing
* Authorization testing
* Input validation testing
* Dependency security testing
* Runtime vulnerability testing

Explain how testing is integrated into the DevSecOps pipeline.

---

### 4.2 Security Test Cases

Create a table similar to:

| Test ID | Description                   | Expected Result             |
| ------- | ----------------------------- | --------------------------- |
| ST-01   | Unauthorized endpoint access  | Access denied               |
| ST-02   | Invalid JWT token             | Authentication failure      |
| ST-03   | Invalid input validation      | Validation error            |
| ST-04   | Dependency vulnerability scan | No critical vulnerabilities |
| ST-05   | Secret detection              | No secrets detected         |
| ST-06   | DAST scan                     | No high-risk findings       |
| ST-07   | Security headers verification | Required headers present    |

Discuss how each test was validated.

Include screenshots of test execution and reports.

---

## 5. Security Assessment

Provide an overall security assessment of the application.

Possible topics:

### Strengths

* Automated security checks
* Secure authentication mechanisms
* Dependency monitoring
* CI/CD integration
* Security testing automation

### Limitations

* Features not implemented
* Known issues
* Future improvements

### Future Work

Examples:

* Implement IAST tooling
* Improve container security
* Add penetration testing
* Introduce infrastructure scanning

---

## 6. Conclusion

Summarize the work performed during Sprint 2.

Highlight:

* DevSecOps adoption
* Automated security validation
* Testing improvements
* Deployment automation
* Security posture of the application

Conclude by emphasizing how the sprint contributed to a more secure and reliable software delivery process.

---
