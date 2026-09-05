# Design Note: GitHub Issues Wrapper

## Architecture Overview
The service is built using **Java 17 and Spring Boot 3**. It acts as a lightweight proxy and webhook processor for a single GitHub repository. The application is divided into standard MVC layers:
- **Controllers** (`IssueController`, `WebhookController`): Handle HTTP request mapping and responses.
- **Service** (`GitHubService`, `WebhookSignatureValidator`): House the core business logic, API calls using `WebClient`, and cryptographic verification.
- **Data Access** (`WebhookEventRepository`): Spring Data JPA interface mapping to an embedded H2 database.

## Error Mapping Strategy
We implemented a `@ControllerAdvice` (`GlobalExceptionHandler`) to intercept exceptions globally:
1. **GitHub API Errors (`WebClientResponseException`)**: When the GitHub API returns a 4xx or 5xx (e.g., 401 Unauthorized, 404 Not Found, 429 Too Many Requests), Spring's WebClient throws this exception. We catch it, extract the status code and body from GitHub, and return a consistent JSON error structure to the client without exposing internal Java stack traces.
2. **Validation Errors (`MethodArgumentNotValidException`)**: When clients send invalid JSON (e.g., missing issue titles), Pydantic/Spring-validation throws this. We extract the exact fields that failed and return a detailed 400 Bad Request.

## Pagination Strategy
GitHub uses the `Link` HTTP header for pagination (e.g., `rel="next"`, `rel="last"`). Instead of parsing this and creating a custom JSON pagination wrapper, our proxy forwards the `page` and `per_page` query parameters to GitHub, and extracts the `Link` header from the GitHub response, injecting it directly into the proxy's HTTP response. This strictly preserves GitHub's pagination semantics and allows clients to use standard HTTP link parsing libraries.

## Webhook Deduplication
Webhooks can be delivered multiple times by GitHub (e.g., due to network timeouts). To guarantee idempotency:
- We capture the `X-GitHub-Delivery` header.
- Before processing, we query the embedded H2 database to check if this `deliveryId` already exists.
- If it exists, we immediately return `204 No Content` and skip processing.
- If it doesn't, we parse the event, save the payload summary and `deliveryId` to the database, ensuring that subsequent redeliveries are ignored.

## Security Trade-offs
- **HMAC Constant-Time Compare**: We use `MessageDigest.isEqual` for comparing the calculated HMAC signature with the `x-hub-signature-256` header. This prevents timing attacks where an attacker could deduce the signature by measuring response times.
- **Environment Variables**: Tokens and secrets are strictly loaded via `.env` files or system environments, preventing hardcoded credentials. If `WEBHOOK_SECRET` is missing, the app refuses to start or auto-generates a secure random 256-bit key to prevent defaulting to an insecure state.
- **Trade-off**: The H2 database is currently in-memory. If the app restarts, webhook history (and deduplication keys) are lost. For a production deployment, this should be replaced with a persistent database (e.g., PostgreSQL).
