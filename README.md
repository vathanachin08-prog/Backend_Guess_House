# springboot-jwt-api

Content management service (Spring Boot 3 / Java 21) with JWT auth, backed by PostgreSQL.

## Prerequisites

- Docker & Docker Compose
- (For local, non-Docker runs) JDK 21 and a local PostgreSQL instance

## Configuration

Active profile is controlled by `spring.profiles.active` in `application.yml` (defaults to `local`). Profile-specific files:

- `application-local.yml` — local dev, expects Postgres on `localhost:54322`
- `application-dev.yml` — dev environment, expects Postgres on `localhost:5432`

Key env vars used by the container (see `docker-compose.yml`):

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | HTTP port (default `30033`) |
| `SPRING_UPLOAD_SERVER_PATH` | Directory for uploaded images |
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | Postgres connection |

## Deployment (Docker Compose)

The app and database run as **separate Compose projects** (`docker-compose.yml` at the repo root, `postgres/docker-compose.yml`) that communicate over a shared external-style bridge network named `cf-cms-service`. Neither file marks it `external`, so Compose creates it automatically the first time either project is brought up — whichever you start first wins, the second just attaches to the existing network.

1. Start the database:
   ```bash
   cd postgres
   docker compose up -d
   ```
2. Start the API (from the repo root):
   ```bash
   docker compose up -d --build
   ```
3. Check status / logs:
   ```bash
   docker compose ps
   docker compose logs -f content-management-service
   ```

The API listens on `30033` and exposes:
- Health check: `/actuator/health`
- Swagger UI: `/swagger-ui.html`
- OpenAPI docs: `/v3/api-docs`

Uploaded images are bind-mounted from `./upload/images` on the host into the container.

### Resetting the network

If the shared network ever gets out of sync (e.g. one project's containers can't resolve `postgres`), remove it and let Compose recreate it:

```bash
docker compose down
docker network rm cf-cms-service
```

Then bring both projects back up as in step 1–2.

## CI/CD

`Jenkinsfile` defines a pipeline that SSHes into the target server, pulls the selected branch, and runs:

```bash
docker compose -f docker-compose.yml build --no-cache
docker compose -f docker-compose.yml up -d
```

It does not manage `postgres/docker-compose.yml` — the database is expected to already be running on the target server.


- 1. Login -> Username + Password | Google | Facebook | Github
   - Save to Local Storage
   - Access Token -> 5 Minute Expired
   - Refresh Token -> 90 day
 2. Call other call API with Access Token => JWT (Json Web Token)
  - Access Token Expired
  - Renew Access Token
  - Auto Logout

## Rental Marketplace Domain (Phase 1)

A rental marketplace designed for university students to find safe, trusted rental rooms and for property owners to list and manage rental accommodations.

### User Roles
- `ROLE_STUDENT`: Free student user. Can search/filter rooms, save favorites, submit visit requests, write reviews, and report properties.
- `ROLE_OWNER`: Property owner. Can register properties, create/update rooms, attach facilities, set pricing/availability, and manage visit requests.
- `ROLE_ADMIN`: Platform admin. Can verify/reject properties, manage reports, and inspect all platform data.

### Main Endpoints

#### 1. Public Endpoints (No Auth Required)
- `GET /api/public/properties` — Search published & verified properties (filters: `keyword`, `city`, `district`, `minPrice`, `maxPrice`, `roomType`, `available`, `facility`, `page`, `size`)
- `GET /api/public/properties/{id}` — View property details with available rooms
- `GET /api/public/properties/{propertyId}/reviews` — List reviews for a property
- `GET /api/public/rooms/{id}` — View individual room details and facilities

#### 2. Property & Room Management (Owner)
- `POST /api/app/properties` — Create a new property listing
- `GET /api/app/properties/{id}` — Get property details
- `PUT /api/app/properties/{id}` — Update property (owner verification enforced)
- `DELETE /api/app/properties/{id}` — Delete property (owner verification enforced)
- `GET /api/app/properties/my` — List properties owned by the authenticated owner
- `POST /api/app/properties/{propertyId}/rooms` — Add room to property
- `GET /api/app/properties/{propertyId}/rooms` — List rooms in a property
- `PUT /api/app/rooms/{id}` — Update room details and facilities
- `DELETE /api/app/rooms/{id}` — Delete room

#### 3. Favorites (Student)
- `POST /api/app/favorites` — Save a property or room as favorite (`{ "propertyId": 1 }` or `{ "roomId": 2 }`)
- `DELETE /api/app/favorites/{id}` — Remove from favorites
- `GET /api/app/favorites` — List current student's favorites

#### 4. Visit Requests (Student & Owner)
- `POST /api/app/visit-requests` — Submit visit request (`propertyId`, `roomId`, `requestedDate`, `requestedTime`, `message`)
- `GET /api/app/visit-requests/my` — Student views their visit requests
- `GET /api/app/owner/visit-requests/{propertyId}` — Owner views visit requests for their property
- `PUT /api/app/visit-requests/{id}/accept` — Owner accepts request
- `PUT /api/app/visit-requests/{id}/reject` — Owner rejects request
- `PUT /api/app/visit-requests/{id}/cancel` — Student cancels pending request
- `PUT /api/app/visit-requests/{id}/complete` — Owner marks visit as completed

#### 5. Reviews (Student)
- `POST /api/app/properties/{propertyId}/reviews` — Submit review (`rating` 1-5, `comment`)
- `PUT /api/app/reviews/{id}` — Update own review
- `DELETE /api/app/reviews/{id}` — Delete own review

#### 6. Reports & Admin
- `POST /api/app/properties/{propertyId}/reports` — Student reports a property (`reason`, `description`)
- `GET /api/admin/reports` — Admin lists reports (filter by `status`)
- `PUT /api/admin/reports/{id}` — Admin updates report status (`PENDING`, `REVIEWING`, `RESOLVED`, `REJECTED`)
- `PUT /api/admin/properties/{id}/verify` — Admin verifies property (`PENDING`, `VERIFIED`, `REJECTED`)

### Database Migration
The rental domain schema is managed via Flyway migration:
- `src/main/resources/db/migration/V3__create_rental_domain_tables.sql`
  - Creates tables: `facilities`, `properties`, `rooms`, `room_facilities`, `favorites`, `visit_requests`, `reviews`, `reports`.
  - Seeds roles (`ROLE_OWNER`, `ROLE_STUDENT`) and standard facilities.

### Running Tests
```bash
./gradlew test
```


