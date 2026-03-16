# TicketFlow — Backend API

> Spring Boot REST API for the Buyer–Vendor Ticket Management System

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green?style=flat-square&logo=mongodb)
![JWT](https://img.shields.io/badge/Auth-JWT-blue?style=flat-square)
![Deploy](https://img.shields.io/badge/Deployed-Render-purple?style=flat-square)

---

## 🌐 Live API

```
https://vendor-rendor-ticket-management-system-qir0.onrender.com
```

> ⚠️ Hosted on Render free tier — first request after inactivity may take **30–60 seconds** to wake up.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Reference](#-api-reference)
- [Database Design](#-database-design)
- [Authentication](#-authentication)
- [File Storage](#-file-storage-gridfs)

---

## ✨ Features

- 🔐 JWT-based stateless authentication
- 👥 Role-based access control — BUYER, VENDOR, ADMIN
- 🎫 Full ticket lifecycle management (OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED)
- 📎 File attachments via **MongoDB GridFS** (no external storage service)
- 💬 Per-ticket comment threads
- 📊 Vendor performance analytics
- 📜 Immutable ticket history / audit trail
- 📥 CSV export for admin reporting
- 🗃️ MongoDB Atlas cloud database

---

## 🛠 Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Spring Boot 3.x | Application framework |
| Spring Security | Authentication & authorization |
| JWT (jjwt) | Stateless token auth |
| MongoDB Atlas | Cloud NoSQL database |
| Spring Data MongoDB | Database ORM layer |
| GridFS | Binary file storage in MongoDB |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

---

## 📁 Project Structure

```
src/main/java/com/sankey/ticketmanagement/
│
├── config/
│   ├── SecurityConfig.java          # Spring Security + CORS config
│   ├── JwtFilter.java               # JWT request filter
│   ├── JwtUtil.java                 # Token generation & validation
│   └── UserDetailsServiceImpl.java  # Spring Security user loading
│
├── controller/
│   ├── AuthController.java          # /api/auth/**
│   ├── TicketController.java        # /api/tickets/**
│   ├── DashboardController.java     # /api/dashboard/**
│   ├── UserController.java          # /api/admin/users/**
│   ├── ProfileController.java       # /api/profile/**
│   ├── CommentController.java       # /api/tickets/{id}/comments
│   └── VendorStatsController.java   # /api/admin/vendors/stats
│
├── service/
│   ├── AuthService.java
│   ├── TicketService.java
│   ├── UserService.java
│   ├── CommentService.java
│   ├── DashboardService.java
│   ├── VendorStatsService.java
│   └── FileStorageService.java      # GridFS operations
│
├── repository/
│   ├── UserRepository.java
│   ├── TicketRepository.java
│   ├── TicketHistoryRepository.java
│   └── CommentRepository.java
│
├── model/
│   ├── User.java
│   ├── Ticket.java                  # includes GridFS fileId ref
│   ├── TicketHistory.java
│   ├── Comment.java
│   ├── Role.java                    # Enum: BUYER, VENDOR, ADMIN
│   ├── TicketStatus.java            # Enum: OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED
│   └── Priority.java                # Enum: LOW, MEDIUM, HIGH
│
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── CreateTicketRequest.java
│   ├── UpdateStatusRequest.java
│   ├── UpdateProfileRequest.java
│   ├── AddCommentRequest.java
│   └── VendorStatsResponse.java
│
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── UnauthorizedException.java
│
└── payload/
    └── ApiResponse.java             # Generic { success, message, data } wrapper
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB Atlas account (or local MongoDB)

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd ticket-management-backend
```

### 2. Configure MongoDB

Open `src/main/resources/application.properties` and set:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<dbname>
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will start at `http://localhost:8080`

### 4. Test the API

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin User","email":"admin@test.com","password":"password123","role":"ADMIN"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"password123"}'
```

---

## ⚙️ Environment Variables

| Property | Description | Example |
|---|---|---|
| `spring.data.mongodb.uri` | MongoDB Atlas connection string | `mongodb+srv://...` |
| JWT secret | hardcoded in application.properties | Change before production |

### `application.properties`

```properties
spring.data.mongodb.uri=YOUR_MONGODB_URI_HERE

# File upload limits (for GridFS)
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=15MB

# Server port
server.port=8080
```

---

## 📡 API Reference

All responses follow this envelope:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { }
}
```

### 🔓 Auth — Public

| Method | Endpoint | Body |
|---|---|---|
| POST | `/api/auth/register` | `{ name, email, password, role }` |
| POST | `/api/auth/login` | `{ email, password }` → returns JWT |

### 🎫 Tickets — Authenticated

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/tickets` | ALL | Paginated list (role-filtered). Query: `page, size, status, priority, search, sortBy, direction` |
| POST | `/api/tickets/create` | BUYER | Create ticket (multipart/form-data with optional file) |
| GET | `/api/tickets/{id}` | ALL | Get ticket by ID |
| PUT | `/api/tickets/{id}/assign` | ADMIN | Assign to vendor. Body: `{ vendorId }` |
| PUT | `/api/tickets/{id}/status` | VENDOR | Update status. Body: `{ status, resolutionNote? }` |
| PUT | `/api/tickets/{id}/close` | BUYER | Close a RESOLVED ticket |
| GET | `/api/tickets/{id}/history` | ALL | Status change audit trail |
| GET | `/api/tickets/export` | ADMIN | Download CSV |
| GET | `/api/tickets/{id}/comments` | ALL | Get comments |
| POST | `/api/tickets/{id}/comments` | ALL | Add comment. Body: `{ message }` |
| DELETE | `/api/tickets/comments/{id}` | ADMIN/Author | Delete comment |

### 📎 Attachments

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/tickets/{id}/attachment/download` | ALL | Stream file from GridFS |

### 📊 Dashboards

| Method | Endpoint | Role |
|---|---|---|
| GET | `/api/dashboard/buyer` | BUYER |
| GET | `/api/dashboard/vendor` | VENDOR |
| GET | `/api/dashboard/admin` | ADMIN |

### 👥 Users & Profile

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/admin/users` | ADMIN | All users |
| PUT | `/api/admin/users/{id}/toggle` | ADMIN | Toggle active status |
| DELETE | `/api/admin/users/{id}` | ADMIN | Delete user |
| GET | `/api/profile` | ALL | Own profile |
| PUT | `/api/profile` | ALL | Update profile |
| GET | `/api/admin/vendors/stats` | ADMIN | Vendor performance stats |

---

## 🗃️ Database Design

### Collections

| Collection | Description |
|---|---|
| `users` | Registered buyers, vendors, admins |
| `tickets` | Support tickets with lifecycle fields |
| `ticket_history` | Immutable audit log of status changes |
| `comments` | Per-ticket discussion threads |
| `fs.files` | GridFS file metadata |
| `fs.chunks` | GridFS binary file chunks (255KB each) |

### Ticket Status Flow

```
OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
 ↑         ↑            ↑            ↑          ↑
BUYER    ADMIN        VENDOR       VENDOR      BUYER
```

---

## 🔐 Authentication

- **Type:** Stateless JWT (JSON Web Token)
- **Algorithm:** HMAC-SHA256
- **Expiry:** 1 hour
- **Header:** `Authorization: Bearer <token>`
- **Password hashing:** BCrypt (strength 10)

### How it works

1. User logs in → receives JWT
2. JWT contains `email` and `role` claims
3. Every request passes through `JwtFilter`
4. Filter validates token and sets `SecurityContext`
5. `@PreAuthorize` annotations enforce role restrictions

---

## 📎 File Storage (GridFS)

Files are stored directly in MongoDB using **GridFS** — no external storage service (no S3, no Cloudinary).

### How it works

```
Client uploads file (multipart/form-data)
        ↓
Spring Boot → FileStorageService.uploadFile()
        ↓
GridFS splits file into 255KB chunks → fs.chunks
Metadata stored → fs.files
        ↓
fileId (ObjectId) saved in Ticket document
```

### Supported file types

`.pdf`, `.doc`, `.docx`, `.xls`, `.xlsx`, `.png`, `.jpg`, `.jpeg`, `.gif`, `.txt`, `.csv`, `.zip`

### Size limit

**5MB** per file (enforced in `FileStorageService`)

---

## 🚢 Deployment (Render)

1. Connect your GitHub repository to Render
2. Set **Build Command:** `./mvnw clean package -DskipTests`
3. Set **Start Command:** `java -jar target/*.jar`
4. Add environment variable: `SPRING_DATA_MONGODB_URI`
5. Add your Vercel frontend URL to `CorsConfig.java` allowed origins

---

## 📝 License

This project is for educational and demonstration purposes.
