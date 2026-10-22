# CollabDesk

A Spring Boot web application for video creators and brands to manage sponsored-content
collaborations: proposal → negotiation → script review → publishing → payment.

IN6206 Internet & Web Applications Development — Individual Assignment (AY2026/27 Sem 1)

See [`docs/design.md`](docs/design.md) for requirements, state machine and database design.

## Tech stack

Java 17 · Spring Boot 3.3 · Spring MVC + Thymeleaf · Spring Data JPA (Hibernate) ·
Spring Security · Bean Validation · springdoc-openapi (Swagger UI) · H2 / MySQL 8 · Lombok · Maven

## Run (IntelliJ IDEA)

1. **File → Open** and select the `collabdesk` folder (the one with `pom.xml`). Wait for Maven to download dependencies.
2. If IntelliJ asks to enable **annotation processing** (Lombok), click *Enable*.
3. Run `CollabDeskApplication`.
4. Open http://localhost:8080 and log in with a demo account.

By default the app uses an **in-memory H2 database** (no setup; data resets on restart).

### Using MySQL instead

1. Edit `src/main/resources/application-mysql.properties` and set your MySQL password.
2. In IntelliJ: *Run → Edit Configurations → Active profiles* = `mysql`.
3. The `collabdesk` database is created automatically on first start.

### Using Docker (app + MySQL in one command)

```bash
docker compose up --build
```

Then open http://localhost:8080. MySQL runs in a container (host port 3307) and data is kept in a Docker volume.

## Running the tests

In IntelliJ: right-click `src/test/java` → **Run 'All Tests'**, or from a terminal: `mvn test`.

| Test class | What it checks |
|---|---|
| `DealStatusTest` | State machine rules (legal / illegal transitions, closed states) |
| `DealWorkflowTest` | Business rules end-to-end on H2: counter-offer, ownership, publish guard, revision limit, undo window, overdue job |
| `SecurityTest` | URL/role rules with MockMvc, 403 for wrong role, 409 JSON error for illegal transitions |

## Demo accounts

| Username | Password | Role |
|---|---|---|
| creator_amy | pass123 | CREATOR |
| creator_ken | pass123 | CREATOR |
| creator_mei | pass123 | CREATOR |
| brand_bubbletea | pass123 | BRAND |
| brand_techgear | pass123 | BRAND |
| admin | admin123 | ADMIN |

## Useful URLs

| URL | What |
|---|---|
| http://localhost:8080/swagger-ui.html | REST API explorer (log in first) |
| http://localhost:8080/h2-console | DB console — JDBC URL `jdbc:h2:mem:collabdesk`, user `sa`, empty password |

## Project structure

```
com.in6206.collabdesk
├── config          SecurityConfig, TimeConfig (Clock), DataSeeder
├── controller
│   ├── api         REST controllers (/api/**)
│   └── web         Thymeleaf page controllers
├── dto             request / response records
├── entity          JPA entities + enums (DealStatus holds the state machine rules)
├── exception       custom exceptions + ApiExceptionHandler
├── repository      Spring Data JPA repositories
├── scheduler       OverdueScheduler (daily @Scheduled job)
└── service         business logic (DealService, DeliverableService, ...)
```
