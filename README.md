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

## Tech Stack
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
Docker is the recommended way to run NanoLink. The project is designed to run the API, PostgreSQL, and Redis together via Docker Compose.

### Prerequisites
- Docker
- Docker Compose

### Quick Start (Docker - Recommended)

1. Create a `.env` file in the project root:

```bash
POSTGRES_DB=nanolink
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

REDIS_HOST=redis
REDIS_PORT=6379

JWT_SECRET=REPLACE_WITH_BASE64_SECRET
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

APP_BASE_URL=http://localhost:8080
SERVER_PORT=8080
```

2. Start all services:

```bash
docker compose up --build
```

3. API is available at:
- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Docker Services
- `app` -> Spring Boot API (`nanolink-app`)
- `postgres` -> PostgreSQL 16 (`nanolink-db`)
- `redis` -> Redis 7 (`nanolink-redis`)

### Useful Docker Commands
```bash
# Start in background
docker compose up --build -d

# View logs
docker compose logs -f app

# Stop services
docker compose down

# Stop services and remove volumes
docker compose down -v
```

### Run Without Docker (Optional)
If you prefer local execution, install Java, Maven, PostgreSQL, and Redis manually.
Then configure environment variables and run:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

### Environment Variables
Set these variables in `.env` (Docker) or your shell (local):
- `DB_HOST`, `DB_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `JWT_SECRET` (base64-encoded, 256-bit recommended)
- `JWT_ACCESS_TOKEN_EXPIRATION`, `JWT_REFRESH_TOKEN_EXPIRATION`
- `APP_BASE_URL`, `SERVER_PORT`

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

## Usage Examples

### Register
```bash
curl -X POST "$APP_BASE_URL/api/v1/auth/register" \
	-H "Content-Type: application/json" \
	-d '{"email":"user@example.com","username":"user1","password":"password123"}'
```

### Login
```bash
curl -X POST "$APP_BASE_URL/api/v1/auth/login" \
	-H "Content-Type: application/json" \
	-d '{"emailOrUsername":"user1","password":"password123"}'
```

### Shorten URL
```bash
curl -X POST "$APP_BASE_URL/api/v1/shorten" \
	-H "Authorization: Bearer <token>" \
	-H "Content-Type: application/json" \
	-d '{"url":"https://example.com"}'
```

### List My URLs
```bash
curl -X GET "$APP_BASE_URL/api/v1/urls/me" \
	-H "Authorization: Bearer <token>"
```

---

## Ownership & Security
- All URL management endpoints require JWT and enforce that users can only access their own URLs.
- JWT tokens are validated on every request.
- Rate limiting is applied to sensitive endpoints.

---

## API Documentation
- Swagger UI: `/swagger-ui.html` or `/swagger-ui/`
- OpenAPI docs: `/v3/api-docs/`

---
