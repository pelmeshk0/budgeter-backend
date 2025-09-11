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

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expenses` | Create a new expense |
| GET | `/api/expenses/{id}` | Get expense by ID |
| GET | `/api/expenses` | Get all expenses (paginated) |
| PUT | `/api/expenses/{id}` | Update an expense |
| DELETE | `/api/expenses/{id}` | Delete an expense |

### Example Request

```json
POST /api/expenses
{
  "amount": 25.50,
  "category": "WANTS",
  "description": "Coffee grinder",
  "tags": ["FOOD", "SHOPPING"]
}
```

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
- **Docker Deploy**: `docker-deploy.yml` - Build and deploy Docker images

## Project Structure

```
src/
├── main/
│   ├── java/com/radomskyi/budgeter/
│   │   ├── BudgeterApplication.java          # Main application class
│   │   ├── controller/                       # REST controllers
│   │   ├── service/                          # Business logic
│   │   ├── repository/                       # Data access layer
│   │   ├── domain/                           # Entity models
│   │   ├── dto/                              # Data transfer objects
│   │   └── exception/                        # Exception handling
│   └── resources/
│       ├── application.properties            # Default configuration
│       ├── application-docker.properties     # Docker configuration
│       └── application-production.properties # Production configuration
└── test/                                     # Test classes
```
