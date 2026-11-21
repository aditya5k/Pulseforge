# Learning Log

Status levels: **Studied** → **Implemented** → **Revised** → **Interview-ready**

_Last updated: 2026-11-21 (moved over from ChatGPT; statuses are self-reported until verified
with interview questions)._

## Current focus

**JWT authentication.** Verified state of the code on 2026-11-21:
- [x] jjwt dependency added
- [x] `JwtService` — generate token, extract username/role, validate
- [ ] `jwt.secret` / `jwt.expiration` added to config (missing → app fails to start)
- [ ] Login returns a JWT (currently returns user info only)
- [ ] `JwtAuthenticationFilter` (`OncePerRequestFilter`) reads `Authorization: Bearer ...`
- [ ] Register filter in `SecurityConfig`, remove `httpBasic`
- [ ] Return 401/403 as JSON (`AuthenticationEntryPoint`, `AccessDeniedHandler`)
- [ ] Tests for auth flow

Fix first: DB password was committed to the public repo → change it and use `${DB_PASSWORD}`.

## Core Java

| Topic | Status | Notes |
|---|---|---|
| Fundamentals, classes & objects | Studied | |
| OOP (4 pillars) | Studied | Need real-world examples + SOLID |
| Collections | Studied | Revise HashMap internals, equals/hashCode |
| Exception handling | Studied | Checked vs unchecked, custom exceptions |
| Java 8 (streams, lambdas, Optional) | Not started | |
| Multithreading & concurrency | Not started | |
| JVM (memory, GC, class loading) | Not started | |

## Spring & Spring Boot

| Topic | Status | Notes |
|---|---|---|
| Maven, servlets, Spring MVC | Studied | |
| IoC, DI, beans, scopes, lifecycle | Studied | |
| Boot startup / auto-configuration | Studied | Revise auto-config deeply |
| REST controllers, service, repository | Implemented | |
| DTOs + mappers | Implemented | |
| Bean Validation | Implemented | |
| Global exception handling | Implemented | `ResponseStatusException` not handled yet |
| Pagination, sorting, filtering (Specifications) | Implemented | |
| JPA / Hibernate basics | Implemented | |
| Lazy vs eager, N+1 | Not started | `@ManyToOne` in `Incident` is EAGER by default |
| `@Transactional`, propagation, rollback | Not started | Missing in `IncidentService` |
| JUnit 5 + Mockito, controller tests | Implemented | |

## Spring Security

| Topic | Status | Notes |
|---|---|---|
| Security fundamentals | Studied | |
| User entity/repo, register, login | Implemented | |
| BCrypt | Implemented | |
| JWT | In progress | See "Current focus" |
| RBAC / `@PreAuthorize` | Not started | |

## PulseForge — pending features

| Feature | Priority |
|---|---|
| Complete JWT | Very high |
| RBAC | Very high |
| Spring AI + RAG, AI incident analysis, support assistant | Very high |
| Redis caching | High |
| Kafka async events | High |
| Docker, CI/CD, cloud deployment | High |
| WebSockets (real-time updates) | Medium |
| Actuator, monitoring | Medium |

## DSA (Striver A2Z)

Last dated checkpoint: **231 / 474** (133 Easy · 96 Medium · 2 Hard), 10 Nov.
Needs updating with the real current count.

| Pattern | Status |
|---|---|
| Arrays & strings | |
| Hashing | |
| Two pointers / sliding window | |
| Binary search | |
| Recursion & backtracking | |
| Linked lists | |
| Trees & graphs | |
| Dynamic programming | |

## SQL

Joins, subqueries, indexes, query optimization — practical exposure, interview depth needed.

## System Design

| Topic | Status |
|---|---|
| Client-server, REST, layered architecture | Studied |
| Database design | Implemented (PulseForge) |
| Caching, queues, load balancing, scaling | Not started |
| Replication, sharding, rate limiting | Not started |
| Microservices, distributed systems | Not started |

Case studies to do: URL shortener · notification service · incident management platform ·
ticket booking · chat app.

## Interview prep

| Area | Status |
|---|---|
| Java questions (50–70) | Not started |
| Spring Boot questions | Not started |
| Project walkthrough (explain PulseForge end to end) | Not started |
| Mock interviews completed | 0 |
| Behavioral | Not started |
| Resume | Not started |

## Session notes

- **2026-11-21** — Moved from ChatGPT. Reviewed the code: JWT only half done, secrets in
  properties, missing jwt config. Next: finish JWT.
