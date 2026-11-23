# Learning Log

Status levels: **Studied** → **Implemented** → **Revised** → **Interview-ready**

_Last updated: 2026-11-22 (moved over from ChatGPT; statuses are self-reported until verified
with interview questions)._

## Current focus

**JWT authentication.** Verified state of the code on 2026-11-21:
- [x] jjwt dependency added
- [x] `JwtService` — generate token, extract username/role, validate
- [x] `jwt.secret` / `jwt.expiration` added to config via `${JWT_SECRET}` env var (2026-11-21)
- [x] Login returns a JWT (`LoginResponse`) — Implemented, not yet tested end-to-end
- [x] `JwtAuthenticationFilter` (`OncePerRequestFilter`) reads `Authorization: Bearer ...`
- [x] Register filter in `SecurityConfig` (`addFilterBefore`), `httpBasic` removed, STATELESS sessions
- [~] 401 when not logged in: `HttpStatusEntryPoint` returns status only, **no JSON body yet**;
  `AccessDeniedHandler` not added
- [ ] Tests for auth flow

Done: DB password now comes from `${DB_PASSWORD}`. Still to do: rotate the DB password + JWT secret (both were exposed).

**RBAC (started 2026-11-21).** Plan reviewed against the code; owner is writing it:
- [x] `@EnableMethodSecurity` on `SecurityConfig`
- [x] Roles get the `ROLE_` prefix (`.roles(...)` in `CustomUserDetailsService`), so `hasRole` works
- [x] `app.admin.email=${ADMIN_EMAIL:}` / `app.admin.password=${ADMIN_PASSWORD:}` in properties
- [ ] `config/AdminSeeder` (`CommandLineRunner`). **File not created yet, so no admin exists**
- [ ] `ADMIN_EMAIL` / `ADMIN_PASSWORD` in IntelliJ run config
- [~] `@PreAuthorize` on Incident ✅ (2026-11-22, Claude-written; create/update: ADMIN+DEVELOPER, delete: ADMIN), still pending on
  MonitoredService (create/update/delete: ADMIN)
- [ ] `AccessDeniedException` → 403 `ErrorResponse` in `GlobalExceptionHandler`
  (use `org.springframework.security.access.AccessDeniedException`, not the `java.nio` one)
- [ ] Tests: `@WithMockUser`, VIEWER gets 403 on POST
- [ ] Verify: admin login returns `"role": "ADMIN"`

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
| Lazy vs eager, N+1 | Implemented (Claude-written) · quiz ✅ | 2026-11-22: `Incident.service` → `LAZY` + `@EntityGraph(attributePaths = "service")` on `findAll(spec, pageable)`. Quiz answer good. Still to do: count queries before/after in the logs yourself; `open-in-view` + `LazyInitializationException`; `@EntityGraph` vs `JOIN FETCH` |
| `@Transactional`, propagation, rollback | Implemented (Claude-written) · quiz ✅ | Added to `IncidentService` + `MonitoredServiceService.deleteService` on 2026-11-21. Proxy mechanism answered well (2026-11-22). Dirty-checking experiment pending; propagation/rollback rules not studied yet |
| JUnit 5 + Mockito, controller tests | Implemented | |

## Spring Security

| Topic | Status | Notes |
|---|---|---|
| Security fundamentals | Studied | |
| User entity/repo, register, login | Implemented | |
| BCrypt | Implemented | |
| JWT | In progress | See "Current focus". Quiz 2026-11-22: signature ✅; filter role ⚠️; 401 vs 403 ⚠️ |
| RBAC / `@PreAuthorize` | Studied | Plan reviewed 2026-11-21; implementation in progress |

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
- **2026-11-21** — Startup failed with "Unable to determine Dialect without JDBC metadata".
  Real cause: `Access denied` from MySQL (hidden further down in `Caused by:`). Two bugs:
  (1) the secret value went inside `${...}` when it should have been the env var *name*;
  (2) ~137 trailing spaces after `${DB_PASSWORD}`: `.properties` values keep trailing
  whitespace. Also, the stale `target/classes` copy needed a resource rebuild.
  **Weak spots to revise:** placeholder syntax, reading the root `Caused by:`, why Hibernate
  needs a DB connection at startup.
- **2026-11-21** — App starts again (MySQL + JWT secret from env vars). Started RBAC: reviewed
  the `@PreAuthorize` + `AdminSeeder` plan against the code, and it fits. Checked admin
  creation: **not created**, because `AdminSeeder.java` doesn't exist yet and the `ADMIN_*` env vars
  aren't set. Key points learned: `hasRole('X')` checks for `ROLE_X`; `@RestControllerAdvice`
  only catches `@PreAuthorize` denials, while URL-rule denials need an `AccessDeniedHandler`;
  `${VAR:}` = empty default so startup doesn't crash.
  **Open interview questions:** hasRole vs hasAuthority · full request path when a VIEWER calls
  DELETE · why `CommandLineRunner` runs after the context is ready.
  **Next:** write AdminSeeder + set env vars → add `@PreAuthorize` + 403 handler → tests.
- **2026-11-21** — 409 on deleting a service that still has incidents. Written by Claude at the
  owner's request (not yet written by the owner): `IncidentRepository.existsByService_Id`,
  check + `ResponseStatusException(CONFLICT)` in `MonitoredServiceService.deleteService`,
  `ResponseStatusException` + `DataIntegrityViolationException` handlers (DB error is logged
  only). Status: **Implemented (not owner-written, not yet tested)**.
  Key ideas: check-then-act race; FK is the source of truth; the FK error surfaces at
  **commit** (after the method returns), so a try/catch around `deleteById` won't catch it;
  `@Transactional` at REPEATABLE_READ does not prevent the race.
  **Found:** `IncidentControllerTest` (7 tests) fails to load the context because
  `@WebMvcTest` picks up `JwtAuthenticationFilter`, but `JwtService` isn't in the slice. This
  started when the JWT filter was added, not with the 409 change. Fix: `@MockitoBean` the
  filter's dependencies in the test.
  **To do:** `MonitoredServiceServiceTest` (409 path, 404 path, happy path).
- **2026-11-22** — N+1 fix applied by Claude at the owner's request: `@ManyToOne(fetch = LAZY)`
  on `Incident.service` + `@EntityGraph` override of `findAll(Specification, Pageable)` in
  `IncidentRepository`. It compiles, and the H2 repo/service tests pass. Still failing (not from
  this change): `contextLoads` needs MySQL, and the `IncidentControllerTest` slice is missing `JwtService`.
  **Quiz score: 3.5 / 5.**
  | Topic | Result |
  |---|---|
  | JWT signature | ✅ Good |
  | JWT filter role | ⚠️ Revise: the filter **identifies** the user (sets the `SecurityContext`); it doesn't **block**. Blocking is done by the authorization rules / `@PreAuthorize`. A bad or missing token means the request goes on as anonymous |
  | 401 vs 403 | ⚠️ You know the definitions; practise applying them to real requests (no/expired token → 401; valid VIEWER token on DELETE → 403) |
  | `@Transactional` proxy | ✅ Good |
  | N+1 | ✅ Good |
  **Next:** re-quiz filter role + 401/403 with PulseForge scenarios; fix `IncidentControllerTest`
  (`@MockitoBean JwtService`); count the N+1 queries before and after yourself.
- **2026-11-22** — `IncidentControllerTest` fixed: **7/7 passing**. The owner had already added
  `@MockitoBean JwtService` + `CustomUserDetailsService` and `addFilters = false`. The file
  didn't compile because the package/import block was pasted twice and there was an extra `}`
  at the end. Lesson: read the compiler error first, because a duplicate `package` line means a
  copy-paste problem, not a Spring problem.
- **2026-11-22** — Owner added `SecurityIntegrationTest` (8 tests) + `src/test/resources/application.properties`
  (H2 + fake JWT key). Full suite: **33 run, 31 pass, 2 fail**. `contextLoads` passes now that
  tests use H2. The 2 failures are **real findings, not test bugs**: VIEWER POST and DEVELOPER DELETE
  return 404 instead of 403, because `IncidentController` has **no `@PreAuthorize` yet**, so any
  logged-in user reaches the service. Auth (401) side is fully working: no token, wrong password,
  tampered token, and valid token all behave correctly.
  **Next:** owner adds `@PreAuthorize` to `IncidentController` → both tests should go green.
- **2026-11-22** — `@PreAuthorize` added to `IncidentController` (Claude-written, at the owner's request):
  POST/PUT `hasAnyRole('ADMIN','DEVELOPER')`, DELETE `hasRole('ADMIN')`, GET open to any
  logged-in user. Full suite **33/33 green**, including both 403 tests. Status: **Implemented
  (not owner-written)**. Still to do for RBAC: `@PreAuthorize` on `MonitoredServiceController`,
  JSON 403 body (`AccessDeniedException` handler), AdminSeeder env vars.
