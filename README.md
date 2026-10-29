# Travel Side Quests

Places worth the detour that never make anyone's top ten — and, for each one, where the
recommendation came from.

Anyone can submit a place. Submissions are held in a review queue and stay invisible to the
public until a moderator approves them.

## Layout

| Path | What |
|---|---|
| `src/main/kotlin` | Spring Boot 4 / Kotlin API |
| `src/main/resources/db/migration` | Flyway schema and seed data |
| `src/test/kotlin` | Testcontainers-backed integration tests |
| `frontend/` | React + Vite + TypeScript app |

## Running it

```bash
cp env.example .env   # then fill in the blanks
docker compose up -d
./gradlew bootRun
```

The app reads its configuration from the environment, so `bootRun` needs the variables from
`.env` exported — in IntelliJ, the EnvFile plugin does this; from a shell, `set -a && source
.env && set +a` before the command.

Then, in a second terminal:

```bash
npm install --prefix frontend && npm run dev --prefix frontend
```

The frontend is at http://localhost:5173 and proxies `/api` to the backend, so CORS does not
come into play in development. API docs are at http://localhost:8080/swagger-ui.html.

```bash
./gradlew test
```

Tests start their own Postgres through Testcontainers, so Docker has to be running. They do
not touch the database in `compose.yaml`.

## How it fits together

- **`country` → `destination` → `side_quest`**, with categories (many-to-many), images and
  sources hanging off the side quest.
- **The URL is the filter** on the browse page, so the home page's search box and its
  category links are plain hrefs and any result list can be shared. Free text (`?q=`) is
  matched against the name, the description, the destination and the country.
- **Countries and categories are closed lists.** Countries are seeded from ISO 3166-1 and
  categories are curated, so a submitter picks from them rather than inventing entries that
  would show up in the public filters before anyone had approved anything.
- **Destinations are the one thing a submitter creates**, matched case-insensitively against
  the existing ones so "westfjords" finds "Westfjords".
- **Images are uploaded first** to `POST /api/v1/images`, which returns a key the submission
  then references. Bytes are stored in Postgres, in their own table so that listing side
  quests never drags whole photos along. Every upload is decoded and re-encoded, which proves
  the file really is a JPEG or PNG and strips the EXIF block — holiday photos carry the GPS
  coordinates of wherever they were taken.
- **Moderation** lives under `/api/v1/admin/**`, behind HTTP Basic with the single account in
  `ADMIN_USER` / `ADMIN_PASSWORD`. That is a placeholder for real accounts, not a substitute
  for them.

## Things worth knowing before changing it

- `spring.jpa.hibernate.ddl-auto: validate` means the entities and the Flyway schema must
  agree. If startup fails with a schema validation error, write a migration rather than
  loosening the setting.
- Bean Validation constraints on request DTOs **must** carry an explicit `@field:` target.
  Kotlin's default puts the annotation on the constructor parameter, where Hibernate Validator
  never looks, and validation silently does nothing. `SideQuestSubmissionTest` has a test that
  fails if this regresses.
- Every public read composes `SideQuestSpecifications.approved()`. There is a test asserting a
  pending quest is absent from every public endpoint; keep it passing when adding read paths.
