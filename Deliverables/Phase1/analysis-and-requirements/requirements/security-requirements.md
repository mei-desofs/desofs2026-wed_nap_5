# Security Requirements

## Authentication

SR1: Passwords must be stored using strong, salted hashing algorithms   
SR1.1: Passwords must not contain context-specific words or easy-to-guess patterns (e.g., "learningmore", "isep", "desofs")
SR1.2: Passwords must be checked against a set of known breached passwords using a secure API (e.g., Have I Been Pwned)
SR2: The system must enforce secure authentication mechanisms using validated credentials and protected session/token mechanisms  
SR3: The system must prevent brute force attacks

## Authorization

SR4: The system must enforce role-based access control (RBAC)  
SR5: Users must only access resources within their permissions  
SR6: Access to course data must require enrollment validation

## Data Protection

SR7: Sensitive data must not be exposed in logs  
SR8: File storage must be secured outside public directories  
SR9: File access must be restricted based on authorization

## Input Validation

SR10: All inputs must be validated server-side  
SR11: File uploads must be validated (type, size, format)  
SR12: The system must prevent path traversal attacks

## Communication Security

SR13: All communication must be secured using HTTPS  
SR14: Secure headers must be implemented

## Dependency Security

SR15: Third-party dependencies must be monitored for vulnerabilities

## Logging and Monitoring

SR16: The system must log security-relevant events  
SR17: Logs must not contain sensitive information  

## Session Management

SR18: The system shall enforce an inactivity session timeout to automatically terminate sessions after a defined period of user inactivity.
SR19: The system shall enforce an absolute maximum session lifetime, after which users must re-authenticate regardless of activity.
SR20: The system shall define a maximum number of concurrent active sessions per user account.
