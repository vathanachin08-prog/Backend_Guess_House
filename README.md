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

