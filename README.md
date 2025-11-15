# To-Do List Service

A simple task-management REST API built with **Kotlin** and **Spring Boot**.  
The service allows clients to create, update, and retrieve to-do items, while automatically marking items as **past due** when their deadline has passed.

---

## 1. Service Description

This service manages to-do items with the following attributes:

- **description** — text describing the task
- **status** — one of: `not done`, `done`, `past due`
- **creationDatetime** — timestamp of creation
- **dueDatetime** — deadline by which the task must be completed
- **doneDatetime** — timestamp when the task was marked as done (nullable)

### Features

- Create a new to-do item
- Update description, status
- Mark items as *done* or *not done*
- Retrieve a single item by ID
- Retrieve all items (optionally filtered by status)
- Automatically mark overdue items as **past due**:  
  Items with `status = not done` whose `due_datetime` is in the past are automatically marked as **past due**

### Business Rules & Assumptions

- Clients cannot explicitly set a to-do item's status to `past due`  
  (it is controlled exclusively by the scheduler)
- Past-due items cannot be modified
- When a to-do transitions from `not done` → `done`, the system records the timestamp
- When transitioning from `done` → `not done`, the timestamp is cleared
- The service uses an **in-memory H2 database** for simplicity
- A lightweight scheduler checks overdue items every 30 seconds (configurable)

---

## 2. Tech Stack

| Component | Technology |
|----------|------------|
| Language | Kotlin 2.x |
| Framework | Spring Boot 3.x |
| Persistence | Spring Data JPA + H2 (in-memory) |
| DB Migration | Liquibase |
| Build Tool | Gradle (Kotlin DSL) |
| Testing | JUnit 5, Spring Boot Test, MockK |
| Containerization | Docker + Docker Compose |

---

## 3. How-To Guide

### ▶️ Build the Service

```bash
./gradlew clean build
```

This compiles the project, runs tests, and produces a runnable JAR inside:

```
build/libs/
```

---

### ▶️ Run the Automatic Tests

```bash
./gradlew test
```

This runs the entire test suite (unit + integration tests).

---

### ▶️ Run the Service Locally Using Docker

1. Build and start the container:

   ```bash
   docker compose up --build
   ```

2. The REST API becomes available at:

   ```
   http://localhost:8080
   ```

3. The H2 web console (dev profile only):

   ```
   http://localhost:8080/h2-console
   ```

   Use these settings to log in:

   ```
   JDBC URL: jdbc:h2:mem:todo-db
   User: sa
   Password:
   ```

4. To stop containers:

   ```bash
   docker compose down
   ```


---

## Scheduler Configuration (Dev)

The service includes a background scheduler that automatically marks todos as **past due** when their `dueDatetime` has passed.

The scheduler is fully configurable via environment variables:

| Environment Variable | Description |
|----------------------|-------------|
| `SCHEDULER_TODO_PAST_DUE_ENABLED`     | Enables or disables the scheduler (`true`/`false`) |
| `SCHEDULER_TODO_PAST_DUE_INTERVAL_MS` | Interval in milliseconds between checks |

Example from `docker-compose.yml`:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: dev
  SCHEDULER_TODO_PAST_DUE_ENABLED: "true"
  SCHEDULER_TODO_PAST_DUE_INTERVAL_MS: "30000"
```

By default, the scheduler runs every **30 seconds** (configured in `application.properties`).

---

## 4. Architecture Overview

This project follows **Hexagonal Architecture** and applies **Clean Architecture principles** such as a domain-centered design, separation of concerns, and dependency inversion between layers:

- **domain** — pure domain model
- **application** — use cases and business logic
- **adapter.in** — REST controllers (input adapters)
- **adapter.out** — JPA persistence adapters (output adapters)

This design ensures that the domain logic remains independent of infrastructure concerns such as persistence, HTTP and external frameworks.

---