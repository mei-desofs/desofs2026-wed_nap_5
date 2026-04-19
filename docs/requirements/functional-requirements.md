# Functional Requirements

## User Management

FR1: The system shall allow an admin to create users  
FR2: The system shall allow an admin to assign roles (Admin, Professor, Student)  
FR3: The system shall allow users to authenticate using email and password

## Course Management

FR4: The system shall allow professors to create courses  
FR5: The system shall allow professors to update course information  
FR6: The system shall allow students to enroll in courses  
FR7: The system shall allow users to view courses they are enrolled in

## Class Sessions

FR8: The system shall allow professors to create class sessions  
FR9: The system shall allow users to view class sessions

## Resource Management

FR10: The system shall allow professors to upload course materials  
FR11: The system shall allow students to access materials for enrolled courses

## Assignment Management

FR12: The system shall allow professors to create assignments  
FR13: The system shall define deadlines for assignments

## Submission Management

FR14: The system shall allow students to submit assignments  
FR15: The system shall allow professors to view submissions  
FR16: The system shall associate submissions with users and assignments

## Chat System

FR17: The system shall allow creation of course chat rooms
FR18: The system shall allow users to send messages in course chat rooms  
FR19: The system shall allow users to read messages within their course
FR20: The system shall restrict course chat rooms to enrolled students only
FR21: The system shall allow moderators (professors) to delete inappropriate messages
 

## Logging

FR22: The system shall log authentication attempts  
FR23: The system shall log critical actions (uploads, submissions, access)
FR24: The system shall include timestamp information for all log entries.
FR25: The system shall include the user identifier associated with each logged action, where applicable.
FR26: The system shall log the origin of requests, including IP address or service identifier.
