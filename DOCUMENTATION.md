# IssueWrapper - GitHub Issues Service

**Repository Name:** [saliherenyuzbazzozlu/IssueWrapper](https://github.com/saliherenyuzbazzozlu/IssueWrapper)
**Student Name:** Salih Eren Yüzbaşıoğlu, Ameya Mathew

---

## 1. Summary of the Implementation

This project implements a web service that wraps the GitHub REST API for Issues, conforming strictly to the assignment specifications and 12-factor app design principles. The service was built using **Java 21** and **Spring Boot 3.3.4**, utilizing `WebClient` for reactive external API calls and an embedded H2 database to persist webhook events. 

### Key Features Implemented:
* **CRUD on Issues:** The service provides fully functioning endpoints to create issues (`POST /issues`), read issues with pagination (`GET /issues` and `GET /issues/{number}`), update issues (`PATCH /issues/{number}`), and add comments (`POST /issues/{number}/comments`).
* **Webhook Handling & Validation:** Implemented a `/webhook` endpoint that accepts `issues` and `issue_comment` payloads. It uses the `WEBHOOK_SECRET` to compute and verify the `X-Hub-Signature-256` HMAC via a constant-time comparison. Valid events are saved to the database.
* **Error Mapping:** A `@ControllerAdvice` global exception handler intercepts GitHub API errors (e.g., 401, 403, 404) and maps them to a clear JSON error response for the client.
* **Pagination Propagation:** When listing issues, the `Link` headers from GitHub are properly extracted and forwarded back to the client.
* **Health Check & Observability:** Implemented a simple `/healthz` endpoint.
* **OpenAPI 3.1 Contract:** Automated OpenAPI specification generation using `springdoc`. The `openapi.yaml` contract maps all inputs, schemas, and endpoints.
* **Docker Support:** Includes a multi-stage `Dockerfile` and `docker-compose.yml` to package and run the application in a portable container.

---

## 2. Design Notes & Trade-offs

* **Error Mapping:** The application leverages Spring's `@ExceptionHandler` to globally catch `WebClientResponseException` instances. Instead of letting the application crash or return a generic 500 error, it parses GitHub's specific error payload (or raw string) and returns a structured JSON mapping to the client alongside the original HTTP status code.
* **Pagination Strategy:** Rather than reimplementing pagination logic manually, the application acts as an intelligent proxy. It accepts `page` and `per_page` query parameters, passes them directly to GitHub, and extracts GitHub's `Link` response headers (which contain the standard next/prev URLs). It then injects these `Link` headers back into the client's HTTP response.
* **Webhook Deduplication:** To ensure idempotency, the application stores the `X-GitHub-Delivery` ID in the H2 database. Before processing a new webhook, it performs a lookup; if the delivery ID is already present, it skips processing and safely returns `204 No Content`.
* **Security Trade-offs:** The `WEBHOOK_SECRET` is never logged. If the user fails to provide one in the `.env` file, the service employs a fail-safe mechanism to securely generate a 32-byte cryptographic secret at runtime. This prevents the application from booting in an insecure state. Verification uses `MessageDigest.isEqual` to prevent timing attacks.

---

## 3. UI Screenshots & Proof of Execution

### A. Unit Tests Passing
*The following demonstrates that the automated unit test suite executes and passes successfully.*

*[INSERT SCREENSHOT OF THE TERMINAL SHOWING `BUILD SUCCESS` FOR TESTS HERE]*

### B. Application Startup (Docker/Maven)
*The following demonstrates the application starting successfully, extracting the repository name, and generating the webhook secret.*

*[INSERT SCREENSHOT OF THE TERMINAL SHOWING `Tomcat started on port 8080` HERE]*

### C. GitHub Webhook Configuration
*The following shows the Webhook configured in the GitHub repository settings, actively pointing to the secure tunnel.*

*[INSERT SCREENSHOT OF GITHUB WEBHOOK SETTINGS UI SHOWING GREEN CHECKMARK]*

### D. API Test: Fetching Issues (GET /issues)
*The following demonstrates a successful API call to the application, retrieving issues from the configured GitHub repository.*

*[INSERT SCREENSHOT OF POSTMAN OR TERMINAL (cURL) SHOWING 200 OK RESPONSE FOR `/issues`]*

### E. Webhook Delivery Success
*The following demonstrates the webhook payload successfully being processed by the application's `/webhook` endpoint.*

*[INSERT SCREENSHOT OF TERMINAL LOGS SHOWING "Successfully processed issues event..." OR POSTMAN HITTING `/events`]*
