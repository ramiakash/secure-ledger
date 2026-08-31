# Secure Ledger

A transaction ledger with a Spring Boot API and a Vue 3 frontend. Users log in, view their
transaction history, filter it by date, and record new transactions.

Everything is scoped to the logged-in user — nobody can see anyone else's transactions.

## Stack

- Backend: Spring Boot 4.1, Java 21, PostgreSQL 17, Flyway, JWT auth
- Frontend: Vue 3, TypeScript, Vite, Pinia, Tailwind 4
- Both run in Docker

## Running it

You only need Docker.

```
git clone https://github.com/ramiakash/secure-ledger.git
cd secure-ledger
docker compose up --build
```

Then open http://localhost:5173

| | |
|---|---|
| Frontend | http://localhost:5173 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

Log in with `alice` or `bob`, password `Password123!` for both.

The database is seeded with 26 demo transactions — 20 for alice, 6 for bob — so pagination,
sorting and date filtering have something to work with.

Postgres is exposed on port 5433 rather than 5432, because I already had Postgres installed
locally and they collided.

## Testing the API directly

`postman_collection.json` in the repo root has every endpoint. The login request saves the
token automatically, so the other requests work without copying anything.

Swagger UI works too — log in, copy the `accessToken`, click Authorize, paste it.

## Endpoints

```
POST /api/v1/auth/login       get a JWT
POST /api/v1/transactions     record a transaction
GET  /api/v1/transactions     list transactions (paginated)
```

The GET takes `from`, `to`, `page`, `size`, `sort` and `direction` as query parameters.

There is no way to ask for another user's transactions. The API works out who you are from
your token.

## Running tests

```
cd backend
mvnw.cmd test
```

Tests use Testcontainers, so Docker needs to be running. They spin up a real Postgres, run the
real migrations against it, and throw it away afterwards. I avoided H2 because it doesn't behave
like Postgres for NUMERIC, partial indexes or CHECK constraints, so passing tests wouldn't have
meant much.

The main test creates a transaction as alice and then checks bob's list doesn't contain it.

## How it's put together

```
backend/
  auth/          login, JWT issuing
  user/          User entity and Spring Security glue
  transaction/   entity, repository, service, controller, DTOs
  validation/    IBAN and currency validators
  error/         error handling
  config/        security, JWT, OpenAPI

frontend/src/
  api/           axios client and API calls
  stores/        Pinia stores
  views/         pages
  components/    table, filter, modal
```

Packages are grouped by feature rather than by layer, so everything about transactions is in
one folder instead of spread across four.

## Main decisions

**Users can only see their own data.** Two things make this work. The user id comes from the
JWT, never from the request — the create endpoint has no field where you could put one. And
every repository method takes a user id and filters on it in the SQL:

```java
Page<Transaction> findByUserIdAndBookedAtGreaterThanEqualAndBookedAtLessThan(
        UUID userId, Instant from, Instant toExclusive, Pageable pageable);
```

There's no `findById` on that repository. The usual approach is to load the row and then check
who owns it, but that check can be forgotten when someone adds a new endpoint. A missing method
parameter won't compile.

**Index for date range queries.** Every query is "this user's transactions, newest first,
maybe between two dates", so there's one index for it:

```sql
CREATE INDEX idx_transactions_user_booked_at
    ON transactions (user_id, booked_at DESC, id DESC);
```

`user_id` goes first because it's an exact match, so Postgres jumps straight to that user's rows.
`booked_at DESC` next means the date range is just a contiguous read, and the sort order is
already correct so there's no sorting step. `id` at the end breaks ties, otherwise two
transactions with the same timestamp can end up on two different pages.

`EXPLAIN ANALYZE` shows an Index Scan with all three conditions inside `Index Cond` and no Sort
node, which is what you want.

**Flyway owns the database schema.** Hibernate runs with `ddl-auto: validate`, so it can't
change anything — it only checks its mappings match and refuses to start if they don't. Schema
changes are numbered SQL files.

**Money is NUMERIC(19,4) and BigDecimal**, never a float. Floats can't represent decimals
exactly and the rounding errors add up. There's also a `CHECK (amount > 0)` on the column as
well as validation in Java, so the rule holds even if something writes to the database directly.

**IBAN validation does the mod-97 checksum**, not just a regex. A regex accepts
`DE89370400440532013001`, which looks fine but has wrong check digits — exactly the kind of typo
the checksum exists to catch.

**Validation errors return 422 with a list of fields**, so the frontend can show messages
under the right inputs. 400 is only used when the request itself is broken. Errors follow
RFC 9457 (Spring's `ProblemDetail`).

**JWT via Spring Security's built-in support** rather than adding a JWT library and writing a
filter. Signature checking, expiry and clock skew are where JWT bugs happen, and the framework
already does all of it.

**The token is kept in memory, not localStorage.** Anything in localStorage can be read by any
script on the page, so one XSS bug would hand over a working banking token. The downside is that
refreshing the page logs you out.

**Client-side validation mirrors the backend rules** so users get instant feedback, but it's
only for convenience — anyone can skip it with curl. The server rules are the real check. The
IBAN checksum is deliberately only on the server rather than duplicated in two places.

## About "microservice"

The brief calls this a backend microservice. I read that as one standalone deployable service,
which is what this is. Adding a gateway, service discovery or a message bus around a single
service would be a lot of moving parts for no benefit. The scalability that matters here is
stateless auth, so any instance can handle any request, and a query plan that still works when
the table is large.

## Things I left out

- No token refresh. Tokens last an hour and then you log in again.
- No rate limiting on login. In a real deployment that would sit at the gateway.
- Swagger is publicly accessible so it's easy to review. `SWAGGER_ENABLED=false` turns it off.
- No signup. Users come from a migration, since nothing in the brief needed registration.
- The `users` table only has what's actually used. Email, roles and KYC fields would be real in
  production but nothing here reads them, so they'd just be dead columns.
- V4 is demo data. It wouldn't ship to production.

## Configuration

Everything has a local default, so nothing needs setting up to run it.

```
DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD    database connection
JWT_SECRET                                          signing key, min 32 bytes
SWAGGER_ENABLED                                     serve API docs
VITE_API_BASE_URL                                   frontend's API URL
```

No secrets are committed. The app won't start if `JWT_SECRET` is shorter than 32 bytes.
