# Assignment Explanation and Documentation

## Overview of the Assignment

The goal of this assignment was to create a small web service that wraps the GitHub REST API for Issues for a single repository. The application needed to meet the following requirements:
1. **CRUD API**: Expose HTTP endpoints to handle Create, Read, Update, and Delete operations for issues (where Delete is treated as "closing" the issue).
2. **Webhook Handling**: Expose a webhook endpoint to receive payloads from GitHub when `issues` and `issue_comment` events occur, validating the HMAC signature.
3. **OpenAPI 3.1 Contract**: Generate a valid OpenAPI 3.1 contract document for the API endpoints.
4. **Automated Tests**: Provide a test suite verifying basic context and operations.
5. **Containerization**: Provide a Docker setup so the service can be run easily via `docker-compose`.

## Technology Stack

The application was built using **Java** with the **Spring Boot 3.3** framework. We chose Java and Spring Boot because they provide a highly structured, industry-standard ecosystem for building robust REST APIs with dependency injection, configuration management, and declarative data handling.
Key libraries include:
- `spring-boot-starter-web`: Provides Tomcat web server, MVC framework, and Jackson for JSON parsing.
- `spring-boot-starter-webflux`: Used strictly for `WebClient`, a non-blocking, reactive client used to communicate with the upstream GitHub REST API.
- `spring-boot-starter-data-jpa` & `h2`: Used to persist webhook events in an in-memory database to demonstrate webhook processing and tracking.
- `springdoc-openapi-starter-webmvc-ui`: Used to automatically generate the OpenAPI 3.1 schema by inspecting the controller endpoints at runtime.
- `dotenv-java`: Used to read variables directly from `.env`.

---

## Detailed Explanation of the Codebase

### 1. Project Configuration (`pom.xml` & `IssuesWrapperApplication.java`)

The `pom.xml` defines the dependencies necessary for the Spring Boot application. We specifically use Spring Boot 3.3.4.

The main application class `IssuesWrapperApplication` is standard, with one key addition: it uses `io.github.cdimascio.dotenv.Dotenv` to load properties from the `.env` file at startup and injects them into the Spring environment (`System.setProperty`). This allows Spring Boot's `@Value` and `@ConfigurationProperties` to seamlessly consume these values.

### 2. Properties Configuration (`config/GitHubProperties.java`)

The `GitHubProperties` class is marked with `@Configuration` to be managed by Spring. It retrieves the required configuration fields (`github.token`, `github.username`, `github.repo.url`, and `github.webhook.secret`).

**Name Extraction Logic**: 
The assignment requires extracting the repository name from the `GITHUB_REPO_URL`. The property class applies string manipulation inside a `@PostConstruct` lifecycle method. It strips the `.git` extension and parses the string to locate the final portion of the URL path (the repository name).

**Webhook Secret Logic**: 
If the user did not provide a `WEBHOOK_SECRET` in the `.env` file, the application detects this. Instead of failing, it automatically generates a 32-byte secure random string (encoded in Base64), logs it vividly in the console, and instructs the user to configure this generated secret in their GitHub repository settings.

### 3. The WebClient Setup (`config/WebClientConfig.java`)

The `WebClientConfig` class initializes a Spring `WebClient` bean. It is pre-configured with a `baseUrl` set to `https://api.github.com/repos/{owner}/{repo}/`, injecting the parsed username and repository name from `GitHubProperties`.
It also sets the default headers required by GitHub:
- `Authorization: Bearer <token>`
- `Accept: application/vnd.github.v3+json`
- `X-GitHub-Api-Version: 2022-11-28`

This centralized configuration ensures that every downstream HTTP request made to GitHub is fully authenticated and routed to the correct repository out-of-the-box.

### 4. Controller Layer (`controller/IssueController.java`)

The `IssueController` uses `@RestController` and exposes our custom REST API.
Each method is annotated with OpenAPI `@Operation` decorators (from `swagger-v3`) to ensure accurate documentation.

- `GET /issues`: Proxies to GitHub's list issues endpoint, converting the `state`, `labels`, `page`, and `per_page` query parameters.
- `GET /issues/{number}`: Proxies to get a single issue.
- `POST /issues`: Accepts a JSON body, wraps it into a GitHub creation request, and forwards it to GitHub to create the issue.
- `PATCH /issues/{number}`: Accepts a JSON body containing updates (like state = "closed" to satisfy the 'D' in CRUD) and patches the existing issue on GitHub.
- `POST /issues/{number}/comments`: Forwards a request to create a comment on the specified issue.

The controller uses standard Java generic types, but returns Spring's `ResponseEntity<String>` containing the exact upstream JSON from GitHub for perfect fidelity. (Note: Data Transfer Objects (DTOs) exist for the Swagger UI generation schema representation).

### 5. Webhook Handling (`controller/WebhookController.java` & `util/HmacUtils.java`)

The `WebhookController` listens on `POST /webhook` for incoming GitHub event payloads.

**Validation**:
When a payload is received, GitHub provides an `X-Hub-Signature-256` header containing `sha256=<hmac-hash>`.
The application uses the custom `HmacUtils` class to compute the HMAC-SHA256 signature of the raw HTTP request body string, signed using the `WEBHOOK_SECRET`. It then performs a constant-time string comparison against the GitHub-provided signature to prevent timing attacks. If the signatures do not match, the controller responds with a `401 Unauthorized`.

**Event Processing**:
Once validated, the controller reads the `X-GitHub-Event` header. If the event is `issues` or `issue_comment`, it parses the JSON using Jackson (`ObjectMapper`). It extracts the Issue ID, the Action (e.g., "opened", "closed", "created"), and the GitHub delivery ID.

**Database Persistence**:
The application saves these webhook events into an H2 in-memory database using Spring Data JPA (`WebhookEvent` entity and `WebhookEventRepository`). An additional endpoint, `GET /events`, is exposed to fetch the latest parsed events to prove the webhook listener is successfully processing data.

### 6. OpenAPI Contract

By including `springdoc-openapi-starter-webmvc-ui`, the application introspects the Spring REST controllers on startup. It automatically generates an OpenAPI 3.1 compliant schema covering all inputs, outputs, models (DTOs), and query parameters.
This schema was successfully dumped via `curl` against `/v3/api-docs.yaml` and saved as `openapi.yaml` in the project root, fulfilling the contract requirement.

### 7. Deployment and Infrastructure

The `Dockerfile` employs a multi-stage build:
1. **Builder Stage**: It uses `eclipse-temurin:21-jdk-alpine` to execute Maven (`./mvnw package`) and compile the `.jar` without requiring the host machine to have Java installed.
2. **Runtime Stage**: It copies the compiled `.jar` file and the local `.env` file into a minimal JRE image (`eclipse-temurin:21-jre-alpine`).

The `docker-compose.yml` ties this together, exposing port `8080` and passing the necessary environment variables downward into the container instance.
