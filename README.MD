# Budgeter Backend

REST API for expense tracking and budgeting.

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL or Docker for a containerized DB setup

### Local Development
  ```bash
  # Start PostgreSQL (if not already running)
  docker-compose up postgres -d
  
  # Run the application
  mvn spring-boot:run
  ```

### Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Code Quality

```bash
# Check for formatting issues (CI/CD)
mvn spotless:check

# Auto-fix formatting issues
mvn spotless:apply
```

## API Documentation

The API includes interactive Swagger documentation:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

Start the application and visit the Swagger UI to explore all available endpoints, request/response schemas, and test the API directly.

## Configuration Profiles

Supported configuration profiles:

- **`default`**: Used for local development
- **`production`**: Production environment with a stricter configuration

## Monitoring

The application includes Spring Boot Actuator for monitoring:

- **Health Check**: `GET /actuator/health`
- **Application Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

## CI/CD

- **Basic CI**: `basic-ci.yml` - Run tests on pull requests
