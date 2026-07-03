# LifeOS Backend Server ⚡

LifeOS Backend is a premium, high-performance REST API server built using **Spring Boot 3**, **Java 21**, and **PostgreSQL**. The codebase is structured using **Hexagonal Architecture (Ports and Adapters) / Pure DDD (Domain-Driven Design)** to ensure complete decoupling of core business logic from framework details, databases, and UI representations.

---

## 🏗️ Architecture Design (Hexagonal / Ports & Adapters)

The project structure is organized by Bounded Contexts (Features) directly under the root package `com.lifeos.be`. Each feature context is split into three clean layers:

```text
com.lifeos.be/
│
├── core/                               # Core Security, Config, and Shared Utilities
│   ├── config/                         # SecurityConfig, CorsConfig
│   └── security/                       # JWT Service, JWT Filters, UserDetailsService
│
├── planner/                            # Bounded Context: planner
│   ├── domain/                         # 1. PURE DOMAIN LAYER (Bebas Framework)
│   │   ├── model/                      # Pure Domain Entities (Activity.java)
│   │   ├── repository/                 # Outbound Ports Interfaces
│   │   └── service/                    # Domain Services (PlannerDomainService.java)
│   │
│   ├── application/                    # 2. APPLICATION LAYER (Use Cases & Orkestrator)
│   │   ├── service/                    # Application Services (ActivityService.java)
│   │   └── dto/                        # Request/Response Contract DTOs
│   │
│   └── infrastructure/                 # 3. INFRASTRUCTURE LAYER (Technical Details)
│       ├── persistence/                # Outbound Adapters (JPA Entities & Spring Data Repositories)
│       ├── config/                     # Configuration Beans for Domain Services
│       └── web/                        # Inbound Adapters (REST Controllers)
│
├── habit/                              # Bounded Context: Habit tracking
├── prayer/                             # Bounded Context: Prayer times schedule
├── goal/                               # Bounded Context: Weekly goals progress
├── auth/                               # Bounded Context: Registration & Login flows
└── gamification/                       # Bounded Context: User Profile, Levels, and XP
```

### 💎 Key Architectural Principles Applied

1. **Pure Domain Layer**: Classes inside the `domain/` directories are **Plain Old Java Objects (POJOs)**. They contain zero annotations from Spring, Hibernate, or Jackson. All business rules (e.g., leveling formulas, XP gains, task validations) live here.
2. **Ports and Adapters**: 
   - **Ports** are interfaces inside the domain layer (e.g., `ActivityRepository` interface).
   - **Adapters** are implementation classes in the infrastructure layer (e.g., `ActivityRepositoryImpl` which bridges domain requests to Spring Data JPA).
3. **Decoupled Entities**: We explicitly separate **Domain Models** (pure state + behavior) from **JPA Database Entities** (mapping to tables).
4. **Anemic to Rich Model**: Business logic is encapsulated inside entities rather than service helper classes (e.g., `User.gainXp(amount)` manages its own state transition rules).

---

## 🛠️ Technology Stack

* **Core Framework**: Spring Boot 3.3.1
* **Java Version**: OpenJDK 21
* **Database & Persistence**: PostgreSQL, Spring Data JPA, Hibernate
* **Security & Tokens**: Spring Security 6 (Stateless JWT Auth)
* **Authentication**: JJWT (Java JWT) 0.12.5
* **Push Notifications**: Firebase Admin SDK (FCM)
* **Testing Library**: JUnit 5, Mockito, AssertJ, H2 Database (In-Memory for test profile)

---

## 🚦 Getting Started

### Prerequisites

* Java 21 JDK installed
* PostgreSQL instance running locally

### Database Setup

Create a PostgreSQL database named `lifeos`:

```sql
CREATE DATABASE lifeos;
```

Configure database credentials inside [application.yml](src/main/resources/application.yml) if they differ from defaults (`postgres`/`password`).

### Running the Server

Run the Spring Boot application using the Gradle wrapper:

```bash
# Windows
.\gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```

The server will start on port `8080` (accessible at `http://localhost:8080`).

---

## 🧪 Testing Strategy (TDD & Mockito)

All tests mirror the main source structure in the `src/test/java/` directory.

We use **H2 In-Memory Database** with PostgreSQL dialect compatibility for our test lifecycle. This ensures all tests pass successfully offline without requiring a running database server.

Run the test suite:

```bash
# Run JUnit tests
.\gradlew.bat test
```

---

## 📡 API Endpoints Summary

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/signup` | Register a new user | No |
| **POST** | `/api/auth/login` | Authenticate and get JWT | No |
| **GET** | `/api/users/me` | Fetch authenticated profile | Yes (Bearer) |
| **POST** | `/api/users/fcm-token` | Register/update FCM token | Yes (Bearer) |
| **POST** | `/api/users/xp` | Add XP and handle level up | Yes (Bearer) |
| **GET** | `/api/activities` | Get daily timeline tasks | Yes (Bearer) |
| **POST** | `/api/activities` | Create a new task | Yes (Bearer) |
| **PUT** | `/api/activities/{id}/toggle` | Toggle task completion | Yes (Bearer) |
| **DELETE** | `/api/activities/{id}` | Delete a task | Yes (Bearer) |
| **GET** | `/api/habits` | Get all habits | Yes (Bearer) |
| **POST** | `/api/habits` | Create a new habit | Yes (Bearer) |
| **PUT** | `/api/habits/{id}/toggle` | Toggle daily habit log | Yes (Bearer) |
| **GET** | `/api/prayers` | Get daily prayer checklist | Yes (Bearer) |
| **PUT** | `/api/prayers/{id}/toggle` | Toggle prayer completion | Yes (Bearer) |
| **GET** | `/api/weekly-goals` | Get weekly goal cards | Yes (Bearer) |
| **PUT** | `/api/weekly-goals/{id}/increment` | Increment goal progress | Yes (Bearer) |

---

## 🐳 Docker & CI/CD Deployment (Render)

Project ini telah dikonfigurasi untuk deployment berbasis container (Docker) secara otomatis menggunakan GitHub Actions CI/CD ke **Render**.

### 1. Dockerization
Aplikasi menggunakan multi-stage **Dockerfile** untuk keamanan dan efisiensi:
- **Build Stage**: Melakukan compile dan packaging JAR menggunakan Gradle dengan JDK 21.
- **Runtime Stage**: Menggunakan image minimal JRE 21 Alpine yang berjalan dengan non-root user (`appuser`).
- **Health Check**: Menggunakan endpoint Spring Actuator (`/actuator/health`).

Jalankan server & DB secara lokal menggunakan Docker Compose:
```bash
# 1. Salin template environment variables
cp .env.example .env
# Edit file .env dengan kredensial database & JWT_SECRET lokal Anda

# 2. Jalankan stack (PostgreSQL + Spring Boot App)
docker compose up --build
```

### 2. Environment Variables Configuration
Seluruh data sensitif telah dipindahkan ke variabel lingkungan. Pastikan variabel berikut ter-set di file `.env` lokal atau di dashboard **Render (Environment Variables)**:
- `DATABASE_URL`: JDBC database URL (Contoh: `jdbc:postgresql://<host>:5432/<db>?sslmode=require`)
- `DATABASE_USERNAME`: Username database
- `DATABASE_PASSWORD`: Password database
- `JWT_SECRET`: JWT Sign Key minimal 256-bit (Generate via `openssl rand -hex 64`)
- `JWT_EXPIRATION_MS`: Masa berlaku JWT token (default: `86400000` / 24 jam)
- `FIREBASE_CONFIG_PATH`: Path konfigurasi FCM (default: `classpath:serviceAccountKey.json`)
- `SERVER_PORT`: Port server (default: `8080`)
- `SPRING_PROFILES_ACTIVE`: Profil Spring aktif (default: `production`)

### 3. GitHub Actions Workflows (CI/CD)
Terdapat 3 pipeline otomatisasi di `.github/workflows/`:
1. **CI — Run Tests (`ci.yml`)**: Berjalan otomatis di semua feature branch & Pull Request. Menjalankan `./gradlew test`.
2. **Staging Deploy (`staging.yml`)**: Berjalan otomatis ketika push/merge ke branch `develop`. Menjalankan unit test lalu memicu webhook **Render Staging**.
3. **Production Deploy (`production.yml`)**: Berjalan otomatis ketika push/merge ke branch `main`. Menjalankan unit test lalu memicu webhook **Render Production**.

