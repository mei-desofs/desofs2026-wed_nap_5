# Functional Requirements

## User Management

FR1: The system shall allow an administrator to create and manage user accounts.
FR2: The system shall allow an administrator to assign and modify user roles (Admin, Professor, Student).
FR3: The system shall allow users to authenticate securely using their email and password.
FR3.1: The system shall provide a mechanism for users to recover or reset their passwords.

## Course Management

FR4: The system shall allow professors to create new courses with a unique code and title.
FR5: The system shall allow professors to update course descriptions and information.
FR6: The system shall allow students to enroll in available courses.
FR7: The system shall allow users to view a dashboard with their active and enrolled courses.
FR8: The system shall allow professors to organize and upload learning resources to their courses.

## Class Sessions

FR9: The system shall allow professors to create class sessions  
FR10: The system shall allow users to view class sessions

## Resource Management

FR11: The system shall allow professors to upload course materials  
FR12: The system shall allow students to access materials for enrolled courses

## Assignment Management

FR13: The system shall allow professors to create assignments  
FR14: The system shall define deadlines for assignments

## Submission Management

FR15: The system shall allow students to submit assignments  
FR16: The system shall allow professors to view submissions  
FR17: The system shall associate submissions with users and assignments

## Chat System

FR18: The system shall allow creation of course chat rooms
FR19: The system shall allow users to send messages in course chat rooms  
FR20: The system shall allow users to read messages within their course
FR21: The system shall restrict course chat rooms to enrolled students only
FR22: The system shall allow moderators (professors) to delete inappropriate messages
 

## Logging

FR23: The system shall log authentication attempts  
FR24: The system shall log critical actions (uploads, submissions, access)
FR25: The system shall include timestamp information for all log entries.
FR26: The system shall include the user identifier associated with each logged action, where applicable.
FR27: The system shall log the origin of requests, including IP address or service identifier.
