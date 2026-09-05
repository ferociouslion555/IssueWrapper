# IssueWrapper

A Spring Boot wrapper service for the GitHub REST API for Issues, designed for a single GitHub repository.

## Features

1. **CRUD Operations**: Clean HTTP API to retrieve, create, and update issues, as well as create issue comments.
2. **Webhook Handling**: Endpoint to receive, validate (using `HMAC-SHA256`), and parse GitHub webhooks (`issues` and `issue_comment` events).
3. **OpenAPI 3.1 Contract**: Full OpenAPI contract generated at runtime, available in `openapi.yaml`.
4. **Configuration Extraction**: Automatically extracts the repository name from `GITHUB_REPO_URL` and generates a secure fallback `WEBHOOK_SECRET` if one isn't provided.

## Requirements

- Java 17+ (if running locally without Docker)
- Docker and Docker Compose (for containerized execution)
- GitHub Personal Access Token (PAT) with `repo` scope

## Configuration

A `.env` file must be present in the root directory (where `docker-compose.yml` is located) with the following content:

```env
GITHUB_TOKEN=your_personal_access_token_here
GITHUB_USERNAME=your_github_username
GITHUB_REPO_URL=https://github.com/your_username/your_repository.git
# Optional: If you don't provide a WEBHOOK_SECRET, the application will generate a secure one and print it to the logs on startup!
WEBHOOK_SECRET=your_secure_webhook_secret_here
```

## Running the Application

### Using Docker Compose (Recommended)

From the root directory, simply run:

```bash
docker-compose up --build
```

The service will start on `http://localhost:8080`.

### Running Locally with Maven

If you prefer to run it locally without Docker:

```bash
cd issues-wrapper
./mvnw spring-boot:run
```

## API Endpoints

- `GET /issues`: List issues (supports `state`, `labels`, `page`, `per_page` query parameters)
- `GET /issues/{number}`: Get a specific issue by number
- `POST /issues`: Create a new issue
- `PATCH /issues/{number}`: Update an existing issue
- `POST /issues/{number}/comments`: Add a comment to an issue
- `POST /webhook`: Webhook endpoint for GitHub to push events to
- `GET /events`: Retrieve the last 10 webhook events received and stored in the in-memory H2 database

## OpenAPI Specification

The `openapi.yaml` file in the root directory contains the full OpenAPI 3.1 contract for this service.
When the application is running, the Swagger UI is also accessible at:
`http://localhost:8080/swagger-ui.html`

## Testing

To run the automated test suite (unit + context tests):

```bash
cd issues-wrapper
./mvnw test
```
