# CLAUDE.md

This repo is both a portfolio project **and** a learning workspace. The owner is moving from
automation testing to a **Java Backend Developer** role and learns by building PulseForge.
Progress is tracked in `LEARNING_LOG.md` — read it at the start of a session and update it
when a topic changes status.

## How to teach in this repo

- **Teach, don't just do.** Explain the concept first (what, why, how it works internally),
  then let the owner write the code, then review it like an interviewer would.
  Only write full implementations when explicitly asked.
- Use simple, clear English. Prefer short explanations + small code snippets.
- After each feature, ask 2–3 interview questions about it and record weak spots in the log.
- Connect concepts to PulseForge wherever possible ("how would this work in our incident API?").
- Be honest about status. Use these four levels, never just "done":
  **Studied → Implemented → Revised → Interview-ready**.
- Never commit secrets. DB password and JWT secret come from environment variables.

### DSA explanation format (owner's preference — follow this order)

1. Identify the pattern
2. General template for the pattern
3. How to recognize when the pattern applies
4. Brute force
5. Optimal solution
6. Dry run
7. Java code
8. Time & space complexity
9. Edge cases & common mistakes
10. How to approach it in an interview

Organize revision by **pattern**, not by individual question.

## Project: PulseForge

AI-powered support & incident management platform.

**Stack:** Java 17, Spring Boot 4.1, Spring Web MVC, Spring Data JPA / Hibernate, MySQL,
Spring Security, jjwt 0.12.6, BCrypt, Bean Validation, JUnit 5, Mockito, H2 (tests), Maven.

**Package layout** (`com.aditya.pulseforge`):
`controller` · `service` · `repository` · `entity` · `dto` · `mapper` · `specification` ·
`exception` · `security` · `config` · `enums`

**Conventions already used:**
- Constructor injection (no field `@Autowired`)
- Request/Response DTOs + hand-written mapper classes
- JPA Specifications for filtering, `Pageable` for pagination/sorting
- `@RestControllerAdvice` for centralized errors (`ErrorResponse`)

**Run:** `./mvnw spring-boot:run` · **Test:** `./mvnw test`
Required env vars: `DB_PASSWORD`, `JWT_SECRET` (Base64, ≥ 256-bit).

## Roadmap (in order)

1. Finish JWT authentication (current)
2. RBAC / authorization
3. Java interview fundamentals (Collections, Java 8, JVM, multithreading, SQL)
4. Redis caching + Kafka async events
5. Spring AI + RAG (AI incident analysis, support assistant)
6. System design using PulseForge as the reference
7. Docker, CI/CD, Actuator, deployment
8. Mock interviews + resume
