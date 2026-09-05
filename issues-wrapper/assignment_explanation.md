# Assignment Code Explanation

This document explains the purpose of each major component in the `issues-wrapper` codebase.

## 1. `IssuesWrapperApplication.java`
**Purpose**: The entry point of the Spring Boot application.
**Logic**: It contains a custom initialization block inside `main()` that looks for a `.env` file in the parent directory (or current directory) using `dotenv-java`. If found, it loads all variables and registers them as System properties so Spring Boot can access them before starting up.

## 2. `config/GitHubProperties.java`
**Purpose**: Type-safe configuration binding.
**Logic**: Maps `github.token`, `github.owner`, and `github.repo` from `application.properties` (which read from `.env`). It implements `InitializingBean` to run custom logic at startup:
- It parses the `GITHUB_REPO` URL to extract just the repository name.
- It checks for `WEBHOOK_SECRET`. If it's missing, it securely generates a random Base64 string so the app is always secure, even if you forgot to set the secret.

## 3. `config/WebClientConfig.java`
**Purpose**: HTTP Client Setup.
**Logic**: Configures Spring WebFlux's `WebClient`. It sets the base URL to GitHub's API, and automatically attaches the `Authorization: Bearer <TOKEN>` header and the required `Accept` and `X-GitHub-Api-Version` headers to *every* outgoing request. This avoids repeating headers in the service layer.

## 4. `service/GitHubService.java`
**Purpose**: The core service layer interacting with GitHub.
**Logic**: Uses the configured `WebClient` to make asynchronous, non-blocking calls (`Mono<ResponseEntity<T>>`) to GitHub. 
- It handles passing query parameters for pagination (`page`, `per_page`) and filters (`state`, `labels`).
- It extracts the `ResponseEntity` to access status codes and headers (like the `Link` header for pagination) directly from GitHub.

## 5. `controller/IssueController.java`
**Purpose**: REST API Endpoints for Issues.
**Logic**: Exposes standard endpoints (`GET /issues`, `POST /issues`, `PATCH /issues/{number}`, etc.). It maps incoming requests to the `GitHubService`. For lists, it explicitly extracts the `Link` header from the GitHub response and attaches it to its own response to propagate pagination metadata.

## 6. `controller/WebhookController.java`
**Purpose**: Receiving and validating GitHub Webhooks.
**Logic**: Exposes `POST /webhook`. 
- Validates the `x-hub-signature-256` header using `WebhookSignatureValidator`.
- Checks idempotency using `webhookEventRepository.existsByDeliveryId`.
- Parses the JSON payload using Jackson's `ObjectMapper` to extract the `action` and `issue.number`.
- Saves a summary of the event to the embedded database.

## 7. `service/WebhookSignatureValidator.java`
**Purpose**: Security verification.
**Logic**: Re-calculates the HMAC SHA-256 hash of the raw HTTP payload using the `WEBHOOK_SECRET`. It then uses `MessageDigest.isEqual` to compare it with the signature sent by GitHub in a constant-time manner, preventing timing attacks.

## 8. `entity/WebhookEvent.java` & `repository/WebhookEventRepository.java`
**Purpose**: Persistence for webhook debugging and idempotency.
**Logic**: A standard JPA entity and Spring Data repository. They define a table `webhook_events` that stores the delivery ID, event type, action, issue number, and timestamp. The repository provides methods to check if a delivery ID already exists and to fetch the 10 most recent events.

## 9. `exception/GlobalExceptionHandler.java`
**Purpose**: Centralized error mapping.
**Logic**: Intercepts `WebClientResponseException` (thrown when GitHub returns a 4xx or 5xx) and `MethodArgumentNotValidException` (thrown on bad input validation). It reformats these exceptions into clean, consistent JSON error responses rather than returning default HTML error pages or stack traces.

---
*(Note for submission: You can copy-paste the above explanations into your Word document, and insert screenshots of Swagger UI (http://localhost:8080/swagger-ui/index.html) testing the endpoints below this section).*
