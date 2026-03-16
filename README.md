# NanoLink - URL Shortener Service

A simple URL shortener built with Spring Boot and PostgreSQL. It supports user registration, JWT-based authentication, analytics, rate limiting, and strict ownership enforcement ensuring users can only manage their own links.

## Features
- **User registration & login** (JWT authentication)
- **Shorten URLs** with optional custom codes and expiration
- **Ownership enforcement**: users can only manage their own URLs
- **Detailed analytics**: click counts, device/browser/geographic stats
- **Rate limiting** to prevent abuse
- **RESTful API** with OpenAPI/Swagger docs
- **PostgreSQL** for persistence
- **Redis** for caching

## Technology Stack
- Java 17
- Spring Boot 3.3
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Redis (cache, rate limiting)
- Lombok
- Maven
- OpenAPI (Swagger UI)

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL
- Redis

### Environment Variables
Set these variables (or use defaults in `application.yaml`):
- `DB_HOST`, `DB_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `JWT_SECRET` (base64-encoded, 256-bit recommended)
- `APP_BASE_URL` (e.g., http://localhost:8080)

### Build & Run
```bash
# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```

---

## Authentication
- **Register:** `POST /api/v1/auth/register` (email, username, password)
- **Login:** `POST /api/v1/auth/login` (email or username, password)
- **JWT:** All protected endpoints require `Authorization: Bearer <token>`

---

## API Endpoints

### Auth
- `POST /api/v1/auth/register` — Register new user
- `POST /api/v1/auth/login` — Login (returns JWT)

### URL Management (all require JWT)
- `POST /api/v1/shorten` — Shorten a URL (optionally custom code/expiration)
- `GET /api/v1/urls/me` — List your URLs
- `PATCH /api/v1/urls/{shortCode}` — Update URL (active/expiration)
- `PATCH /api/v1/urls/{shortCode}/deactivate` — Deactivate URL
- `PATCH /api/v1/urls/{shortCode}/reactivate` — Reactivate URL
- `DELETE /api/v1/urls/{shortCode}/permanent` — Permanently delete URL
- `GET /api/v1/stats/{shortCode}` — Get analytics for your URL

### Public
- `GET /api/v1/{shortCode}` — Redirect to original URL
- `GET /api/v1/health` — Health check

---

