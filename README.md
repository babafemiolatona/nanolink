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
