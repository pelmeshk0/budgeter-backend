# Budgeter Backend

REST API for expense tracking and budgeting.

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for containerized setup)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd budgeter-backend
   ```

2. **Run with Maven**
   ```bash
   # Start PostgreSQL (if not already running)
   docker-compose up postgres -d
   
   # Run the application
   mvn spring-boot:run
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

The application will be available at `http://localhost:8080`

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

The application supports different configuration profiles:

- **`default`**: Local development with H2 database
- **`test`**: Testing with H2 database
- **`docker`**: Containerized environment with PostgreSQL
- **`production`**: Production environment with PostgreSQL

### Why Separate Docker and Production Configs?

- **Docker Config** (`application-docker.properties`):
  - Optimized for containerized environments
  - Uses container networking (e.g., `postgres:5432`)
  - More verbose logging for debugging
  - Health checks exposed for container orchestration

- **Production Config** (`application-production.properties`):
  - Environment variable-based configuration
  - Strict database validation (`ddl-auto=validate`)
  - Minimal logging for performance
  - Security-focused actuator endpoints
  - External database connections

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
