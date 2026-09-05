# GitHub Issues Wrapper Service

A small Spring Boot service that wraps the GitHub REST API for Issues for a single repository.

## Features
- CRUD on Issues (Create, Read, Update). Note: GitHub doesn't support deleting issues, so "Delete" is implemented as closing the issue.
- Webhook Handling with HMAC SHA-256 signature validation.
- Idempotency for webhooks (prevents processing the same delivery twice).
- Exposes OpenAPI 3.1 Contract.

## Setup

### Environment Variables

The application relies on a `.env` file for configuration. Example:

```env
GITHUB_TOKEN=your_fine_grained_pat
GITHUB_OWNER=your_github_username
GITHUB_REPO=your_repo_name # Can be a name or full URL (e.g., https://github.com/owner/repo.git)
WEBHOOK_SECRET=your_webhook_secret # Will be auto-generated if left blank
PORT=8080
```

> [!NOTE]
> If `WEBHOOK_SECRET` is left empty in the `.env` file, the application will securely generate one on startup and log it to the console. You can use this generated secret when configuring the webhook in GitHub.

### Running Locally (Non-Docker)

Prerequisites: Java 17+, Maven.

```bash
cd issues-wrapper
./mvnw spring-boot:run
```

### Running with Docker

```bash
cd issues-wrapper
docker-compose up --build
```

## API Usage

The service exposes the following endpoints (default port 8080):

### 1. Create an Issue
```bash
curl -X POST http://localhost:8080/issues \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Issue", "body":"This is a test issue created via API"}'
```

### 2. List Issues
```bash
curl "http://localhost:8080/issues?state=open&per_page=10"
```

### 3. Get an Issue
```bash
curl "http://localhost:8080/issues/1"
```

### 4. Update an Issue (Close)
```bash
curl -X PATCH http://localhost:8080/issues/1 \
  -H "Content-Type: application/json" \
  -d '{"state":"closed"}'
```

### 5. Create a Comment
```bash
curl -X POST http://localhost:8080/issues/1/comments \
  -H "Content-Type: application/json" \
  -d '{"body":"This is a comment"}'
```

## Webhooks

The service exposes a `/webhook` endpoint to receive GitHub webhook events.

1. Set up a Webhook in your GitHub repository settings.
2. Payload URL: `https://your-tunnel-url/webhook` (use ngrok, localtunnel, etc. to expose your local server).
3. Content type: `application/json`.
4. Secret: Use the `WEBHOOK_SECRET` from your `.env` (or the one auto-generated on startup).
5. Events: Select "Issues" and "Issue comments".

### Debugging Webhooks
You can view the last 10 processed webhook deliveries by visiting:
```bash
curl http://localhost:8080/events
```

## OpenAPI Specification
The OpenAPI UI is available at `http://localhost:8080/swagger-ui/index.html`.
The raw JSON/YAML spec is available at `http://localhost:8080/v3/api-docs` or `http://localhost:8080/v3/api-docs.yaml`.
A statically generated `openapi.yaml` is provided in the root of the project.

## Tests
To run unit and integration tests:
```bash
cd issues-wrapper
./mvnw test
```
