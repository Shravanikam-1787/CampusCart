# CampusCart – Online College Stationery Store

> **Phase 1: Core Application Foundation**  
> A lightweight, beginner-friendly online college stationery store application designed for academic demonstrations and Software Engineering / DevOps lab curriculum.

---

## 📌 Project Overview & Problem Statement

College students frequently need stationery supplies—such as exam-ruled notebooks, blue/black pens, engineering drawing sheets, lab record folders, calculators, and ID card holders—often on short notice during lecture breaks or late-night assignment submissions. Campus stationery shops frequently face peak-hour queues, out-of-stock items, and fragmented order tracking.

**CampusCart** solves this campus challenge by providing an accessible, responsive web store tailored to college students. Students can browse the campus stationery catalog, filter by academic categories, assemble a cart, and place immediate hostel-delivery or counter-pickup orders without navigating complex external payment gateways or third-party sign-ups.

---

## 🎯 Objectives

1. **Simplicity & Explainability:** Built cleanly using simple Java 17 + Spring Boot and vanilla HTML/CSS/JavaScript so that a 2nd-year engineering student can easily understand, modify, and present the architecture.
2. **Unified Single-Artifact Deployment:** The frontend assets reside directly in `src/main/resources/static/`, enabling the entire application to build into a single self-contained JAR file suitable for simple Dockerization.
3. **Foundation for DevOps Experiments:** Architected to serve as the baseline application for an upcoming 10-stage DevOps and Software Engineering pipeline.

---

## ✨ Main Features

* **Stationery Catalog Browsing:** Browse essential college supplies (notebooks, pens, folders, drafting sheets, scientific calculators, highlighters).
* **Category Filtering:** Filter by academic categories (*Notebooks*, *Pens & Pencils*, *Folders & Organizers*, *Electronics*, *Exam & Drafting*, *Accessories*).
* **Dynamic Cart Management:**
  * Add products with visual confirmation feedback.
  * Live cart item count badge in navigation.
  * Adjust quantities (`+` / `−`) and remove items.
  * Instant subtotal and grand total calculation.
* **Campus Checkout:**
  * Simple student input form (Name, Roll Number / Student ID, College Email, Hostel & Room Number).
  * Campus delivery options (*Cash on Delivery*, *UPI on Delivery*, *Pay at Counter*).
* **Order Confirmation & Tracking:**
  * Generates a unique order identifier (e.g., `CC-1001`).
  * Instant confirmation modal displaying delivery location and summary.
  * Built-in "Track Order" lookup by Order ID.

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Backend** | Java 17+ / Spring Boot 3.3.4 | RESTful APIs, Spring Web, Jakarta Validation |
| **Data Store** | In-Memory Repositories | `ConcurrentHashMap` with pre-loaded college catalog (zero DB setup required) |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript | Responsive flex/grid design, SVG stationery icons, zero heavy UI frameworks |
| **Build Tool** | Apache Maven 3.9+ | Standard Maven layout with JUnit 5 & Spring MockMvc testing |
| **Testing** | JUnit 5 & Spring Boot Test | Automated unit and integration tests |

---

## 📂 Project Structure

```text
CampusCart/
├── .gitignore
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── campuscart/
    │   │           ├── CampusCartApplication.java
    │   │           ├── controller/
    │   │           │   ├── OrderController.java
    │   │           │   └── ProductController.java
    │   │           ├── dto/
    │   │           │   ├── CartItemDto.java
    │   │           │   └── OrderRequest.java
    │   │           ├── model/
    │   │           │   ├── Order.java
    │   │           │   ├── OrderItem.java
    │   │           │   └── Product.java
    │   │           ├── repository/
    │   │           │   ├── OrderRepository.java
    │   │           │   └── ProductRepository.java
    │   │           └── service/
    │   │               ├── OrderService.java
    │   │               └── ProductService.java
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           ├── css/
    │           │   └── styles.css
    │           ├── js/
    │           │   └── app.js
    │           └── index.html
    └── test/
        └── java/
            └── com/
                └── campuscart/
                    ├── CampusCartApplicationTests.java
                    ├── controller/
                    │   ├── OrderControllerTest.java
                    │   └── ProductControllerTest.java
                    └── service/
                        └── OrderServiceTest.java
```

---

## 🚀 How to Run Locally

### Prerequisites
* **Java 17** or newer installed (`java -version`)
* **Maven 3.8+** installed (`mvn -version`)

### 1. Clone the Repository
```bash
git clone https://github.com/Parth9P/CampusCart.git
cd CampusCart
```

### 2. Build and Run Tests
```bash
mvn clean test
```

### 3. Run the Spring Boot Application
```bash
mvn spring-boot:run
```

### 4. Open in Browser
Open your browser and navigate to:
```text
http://localhost:8080/
```

---

## 🔌 Basic API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/products` | Retrieve all stationery products |
| `GET` | `/api/products/{id}` | Retrieve single product details by ID |
| `GET` | `/api/products/category/{category}` | Filter stationery products by category |
| `POST` | `/api/orders` | Place a new order with cart items and student details |
| `GET` | `/api/orders/{orderId}` | Lookup order status and items by Order ID (e.g., `CC-1001`) |
| `GET` | `/api/orders` | List all recent orders |

### Sample Order Payload (`POST /api/orders`)
```json
{
  "customerName": "Rahul Sharma",
  "studentId": "22BCE1042",
  "email": "rahul.sharma@college.edu",
  "hostelRoom": "Hostel C, Room 204",
  "paymentMethod": "Cash on Delivery",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 3 }
  ]
}
```

---

## 📊 Current Implementation Status (Phase 1)

* [x] Core Spring Boot backend architecture initialized
* [x] In-memory product catalog with 10 essential stationery items
* [x] In-memory order management with ID generation (`CC-1001`, `CC-1002`, ...)
* [x] REST API endpoints for catalog and orders
* [x] Responsive student store interface (Catalog, Cart Drawer, Checkout Modal, Order Confirmation, Order Tracking)
* [x] Automated unit and integration tests passing (`10/10 tests passed`)
* [x] Maven build and clean execution verified locally

---

## 🗺️ Future DevOps Integration Roadmap

This application foundation will serve as the subject for the following upcoming lab experiments:

```text
Jira (Agile) ──> Git / GitHub ──> Jenkins CI ──> SonarQube (Quality Gate)
                                       │
                                       ▼
Docker Container ──> Docker Hub ──> Kubernetes Cluster
                                       │
                                       ▼
Ansible Configuration ──> Terraform Infrastructure ──> Selenium Automated Tests
```

1. **Jira:** Agile user stories, backlog grooming, and sprint tracking.
2. **Git/GitHub:** Feature branching, pull requests, and commit conventions.
3. **Jenkins CI:** Automated build, test triggers, and artifact archiving.
4. **SonarQube:** Static code analysis, security scanning, and code coverage gates.
5. **Docker:** Containerizing the packaged Spring Boot application (`Dockerfile`).
6. **Docker Hub:** Automated image push and version tagging.
7. **Kubernetes:** Deployments, Services, ConfigMaps, and Pod scaling.
8. **Ansible:** Automated server setup and configuration management.
9. **Terraform:** Cloud infrastructure provisioning (IaC).
10. **Selenium:** Automated browser end-to-end testing for the purchase workflow.

*(Note: The DevOps stages listed above will be implemented incrementally in subsequent phases).*
