#  FixNow – Smart Home Services Platform

<p align="center">
  <img src="https://img.shields.io/badge/Status-Stable%20Demo%20Release-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Version-v1.0--STABLE-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Architecture-Layered%20MVC%20%2B%20Security-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Type-Spring%20Boot%20Web%20App-success?style=for-the-badge"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-Backend-orange?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-Core-green?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20MVC-Web%20Layer-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Spring%20Security-Session%20Based-red?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Spring%20Data%20JPA-ORM-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Thymeleaf-View%20Engine-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/H2-In%20Memory-lightgrey?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven&logoColor=white"/>
</p>

---

## 📌 Overview

**FixNow** is a full-stack Spring Boot web application designed to connect customers with service technicians for home maintenance tasks such as:

- Plumbing services  
- Electrical repairs  
- Air conditioning maintenance  
- Locksmith services  

The system simulates a real-world service marketplace with role-based access, request dispatching, and real-time communication.

---

## ⚠️ Release Status

This is the **Stable Demo Release (v1.0-STABLE)**.

The system is fully functional for demonstration purposes and academic evaluation.

### ✔️ Stable Features
- Complete layered architecture (Controller → Service → Repository)
- Role-based system (Customer / Technician / Admin)
- Session-based authentication (Spring Security)
- Service request lifecycle (Create → Assign → Accept → Complete)
- Technician matching system
- Chat system between users
- Review & rating system
- Timeline tracking for requests
- Global exception handling
- DTO-based request/response design
- Thymeleaf server-side rendering

---

##  System Architecture

The project follows a **strict layered MVC architecture with separation of concerns**:

### 🔹 Presentation Layer
- Thymeleaf Templates
- HTML/CSS/JS static assets

### 🔹 Controller Layer
Handles HTTP requests and routing:
- AuthController
- CustomerController
- TechnicianController
- AdminController
- ChatController
- ErrorPageController

---

### 🔹 Service Layer (Business Logic)
Core system logic:
- AuthService
- CustomerService
- TechnicianService
- DispatchService
- TechnicianMatchingService
- ChatService
- RequestTimelineService

---

### 🔹 Repository Layer (Data Access)
Spring Data JPA repositories:
- UserRepo
- TechnicianRepo
- ServiceRequestRepo
- MessageRepo
- ReviewRepo

---

### 🔹 Model Layer (Entities)
Database entities:
- User
- Technician
- ServiceRequest
- Message
- Review
- Role (Enum)

---

### 🔹 Security Layer
- Session-based authentication
- Custom filter: `SessionAuthenticationFilter`
- Security configuration via `SecurityConfig`
- Session constants management

---

### 🔹 DTO Layer
Used to separate API/data transfer logic:
- LoginDTO
- RegisterDTO
- ServiceRequestDTO
- MessageDTO
- ReviewDTO
- UserDTO

---

### 🔹 Exception Handling
- GlobalExceptionHandler
- BadRequestException
- ResourceNotFoundException

---

## 🔄 System Flow

Client → Controller → Service → Repository → Database → Response → View

---

##  Tech Stack

- Java 17+
- Spring Boot
- Spring MVC
- Spring Security (Session-based auth)
- Spring Data JPA (Hibernate ORM)
- Thymeleaf
- H2 Database (demo mode)
- Maven
- HTML / CSS / JavaScript

---

## 📂 Project Structure

```text
src/main/java/com/fix/fixnow
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
├── service
└── FixnowApplication.java

src/main/resources
├── application.properties
├── application-dev.properties

src/main/resources/templates
├── login.html
├── register.html
├── customerDashboard.html
├── technicianDashboard.html
├── createRequest.html
├── requestDetails.html
├── addReview.html
├── adminDashboard.html
└── error/
    ├── 403.html
    ├── 404.html
    └── 500.html

src/main/resources/static
├── css/fixnow-theme.css
└── js/fixnow-ui.js

 ## Security Model
Session-based authentication (no JWT in this version)
Role-based access control
Custom authentication filter
Protected routes per role
Secure session tracking via server-side storage
 ## Core Features Explained
🔹 Service Request Lifecycle
Customer creates request
System matches technician
Technician accepts request
Work is completed
Customer leaves review
🔹 Technician Matching System
Automatically assigns technicians based on availability and skill
Managed by TechnicianMatchingService
🔹 Chat System
Real-time message exchange between customer and technician
Stored using Message entity and MessageRepo
🔹 Request Timeline
Tracks each stage of service request
Implemented using RequestTimelineService
 ## Design Principles
Clean layered architecture
Separation of concerns (MVC strict enforcement)
DTO-based communication (no entity leakage)
Stateless controllers
Service-driven business logic
Repository abstraction (no raw SQL)
Centralized exception handling
Secure session management
 ## Demo Readiness

This system is validated for:

Authentication flows
Multi-role access control
Request lifecycle stability
Database consistency (JPA/Hibernate)
Chat & messaging system
Exception handling robustness
 ## Future Improvements
REST API expansion
WebSocket real-time chat upgrade
Payment integration system
Advanced admin analytics dashboard
Cloud deployment (AWS / Azure)
Production-grade JWT authentication
 ## Summary

FixNow is a complete backend-structured Spring Boot system demonstrating:

Enterprise-style layered architecture
Secure session-based authentication
Real-world service marketplace logic
Clean MVC + DTO separation
Scalable backend design principles
