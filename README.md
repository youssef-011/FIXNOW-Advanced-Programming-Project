# FixNow – Home Services Web Application

<p align="center">
  <img src="https://img.shields.io/badge/Status-Under%20Development-yellow?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Version-v1.0-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Layered-Design-lightgrey?style=for-the-badge"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-Backend-orange?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Servlets-Controller-grey?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-Basic%20Setup-green?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/JSP-View-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/JDBC-Data%20Access-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven&logoColor=white"/>
</p>

---

## 📌 Overview

**FixNow** is a web-based application designed to connect users with technicians for home services such as:

* Plumbing
* Electrical maintenance
* Air conditioning repair
* Locksmith services

The project is being developed as part of the **Advanced Programming Applications course**.

---

## ⚠️ Project Status

This project is currently **under development**.

* The current version focuses on **building the system structure**
* Core features are implemented partially
* More functionality will be added in future updates

---

##  Architecture

The system is designed using the **MVC (Model-View-Controller) architecture**:

* **Model** → Represents data and database interaction
* **View** → JSP pages for user interface
* **Controller** → Handles requests using Servlets / Spring Controllers

---

## 🔄 System Flow

User → View (JSP) → Controller → Model → Database → Response → View

---

##  Technologies Used

* Java
* Spring Boot MVC
* Thymeleaf
* Spring Security with session-based demo auth
* Spring Data JPA
* H2 in-memory database for the stable demo profile
* Maven

---

## 📂 Project Structure

```text
src/main/java/com/fixnow
├── controller
├── service
├── repo
├── model
└── FixNowApplication.java

src/main/resources
├── application.properties

src/main/resources/templates
├── login.html
├── register.html
├── customerDashboard.html
├── technicianDashboard.html
├── createRequest.html
├── requestDetails.html
└── addReview.html
```

---

##  Current Progress

* Basic project structure created
* MVC architecture applied
* Thymeleaf pages implemented for the core demo flows
* Database access uses Spring Data JPA with H2 in-memory storage
* Demo users are seeded on startup

---

## Demo Security Note

This stable demo keeps CSRF disabled in `SecurityConfig` so the server-rendered forms
and API smoke tests can run without token plumbing. Enable CSRF and add tokens before
using the app outside a local/demo environment.

---

##  Future Work

* Complete Service Layer logic
* Improve database integration
* Add validation and error handling
* Enhance UI
* Complete system functionality

---

##  Notes

* This version focuses on **understanding architecture and concepts**
* Implementation is still in progress
* The project will evolve as more topics are covered in the course

---
