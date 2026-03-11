# Enable Mock SQL DB + CRUD Practice (Spring Boot 4, Java 21)

## Summary
- Keep Java 21 and Spring Boot 4.0.3, add an in-memory H2 database with JPA auto-DDL, and expose a small CRUD API (Users + Todos) that is easy to extend with new tables.

## Key Changes
- Build config:
  - Keep `java.toolchain` at 21 and Spring Boot `4.0.3`.
  - Replace `spring-boot-starter-webmvc` with `spring-boot-starter-web`.
  - Add `spring-boot-starter-data-jpa` and `com.h2database:h2`.
- App configuration:
  - Add H2 datasource settings in `application.yaml` (jdbc URL, driver, username, password).
  - Enable H2 console and set `spring.jpa.hibernate.ddl-auto=update`.
  - Optional: enable SQL logging for learning (`spring.jpa.show-sql=true`).
- Domain + persistence:
  - Replace current `User` record DTO with a JPA `User` entity (id, name, email, createdAt).
  - Add a `Todo` entity with a `ManyToOne` relationship to `User` (id, title, status, user).
  - Create Spring Data `Repository` interfaces for each entity.
- API layer:
  - Replace the current `UserController` with standard CRUD endpoints.
  - Add a `TodoController` for CRUD operations and a "list by user" query.
  - Fix controller mapping to use `@RequestMapping("/users")` or method-level paths (current `@RestController(value="/users")` is incorrect).
- Seed data:
  - Add a `CommandLineRunner` to insert a few users + todos at startup for quick practice.

## Public Interfaces
- REST endpoints:
  - `GET /users`, `POST /users`, `GET /users/{id}`, `PUT /users/{id}`, `DELETE /users/{id}`
  - `GET /todos`, `POST /todos`, `GET /todos/{id}`, `PUT /todos/{id}`, `DELETE /todos/{id}`
  - `GET /users/{id}/todos`
- Entities:
  - `User` (id, name, email, createdAt)
  - `Todo` (id, title, status, user)

## Test Plan
- `./gradlew test`
- Manual API smoke:
  - Create a user, then create a todo for that user.
  - Fetch user list and user’s todos.
  - Update and delete both entities.

## Assumptions
- Use H2 in-memory database, Spring Data JPA, and JPA auto-DDL.
- Keep Java 21 and Spring Boot 4.0.3 as already configured.
