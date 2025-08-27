# Budgeter Backend

A Spring Boot application providing RESTful API endpoints for managing personal expenses with MongoDB persistence.

## Features

- **CRUD Operations**: Create, Read, Update, and Delete expenses
- **MongoDB Persistence**: Scalable NoSQL database storage
- **Validation**: Input validation with proper error handling
- **RESTful API**: Clean, REST-compliant endpoints
- **OpenAPI/Swagger Documentation**: Interactive API documentation and testing
- **Lombok Integration**: Reduces boilerplate code and improves developer productivity
- **Comprehensive Testing**: Unit tests, integration tests, and service layer tests
- **Modern Architecture**: Layered architecture with clear separation of concerns
- **Development Tools**: VS Code configuration, Maven wrapper, and code formatting

## API Documentation

The application includes comprehensive OpenAPI 3.0 documentation with Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

### Features of the API Documentation:
- Interactive API testing interface
- Detailed request/response schemas
- Example requests and responses
- Parameter descriptions and validation rules
- Response codes and error handling
- Try-it-out functionality for all endpoints

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expense` | Create a new expense |
| GET | `/api/expenses` | Get all expenses |
| GET | `/api/expense/{id}` | Get expense by ID |
| PUT | `/api/expense/{id}` | Update expense by ID |
| DELETE | `/api/expense/{id}` | Delete expense by ID |

## Expense Model

```json
{
  "value": 25.50,
  "category": "NEEDS",
  "description": "Lunch at restaurant",
  "tags": ["FOOD", "BARS_AND_RESTAURANTS"]
}
```

### Categories
- **FIXED** - Recurring, unavoidable expenses that don't change much month to month. Think: rent/mortgage, utilities, insurance, loan payments, subscriptions. Goal: Keep them as low as possible relative to your income, since they reduce flexibility.
- **NEEDS** - Essential expenses you can't avoid, but they may vary in cost. Examples: groceries, transportation (gas, transit), healthcare, childcare. These are non-negotiable for living, but you may have some control (e.g., cooking at home vs. eating out)
- **WANTS** - Everything that's non-essential / lifestyle-driven. Examples: dining out, travel, shopping, entertainment, hobbies, streaming services. These are the most flexible part of your budget—you can cut back here if you need to save or hit a financial goal.

### Tags
The application provides a comprehensive set of tags for detailed expense classification:

- **FOOD** - Food and groceries
- **BARS_AND_RESTAURANTS** - Bars and restaurants
- **TRANSPORT** - Transportation and travel expenses
- **ENTERTAINMENT** - Entertainment and leisure expenses
- **SHOPPING** - Shopping and retail expenses
- **HEALTH** - Healthcare and medical expenses
- **EDUCATION** - Education and learning expenses
- **HOUSING** - Housing and accommodation expenses
- **CLOTHING** - For those fancy sneakers
- **UTILITIES** - Utility and service expenses
- **INSURANCE** - Insurance
- **PETS** - Pet expenses
- **SUBSCRIPTIONS** - Subscriptions and memberships
- **SPORTS_AND_HOBBIES** - Sports and hobbies
- **PERSONAL_CARE** - For you
- **GIFTS** - For the loved ones
- **DONATIONS** - Donations and charity
- **BANKING_AND_TAXES** - Banking and Taxes
- **TRAVEL** - Travel expenses
- **VICES** - Fun stuff
- **OTHER** - Other miscellaneous expenses

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data MongoDB**
- **MongoDB**
- **Maven**
- **SpringDoc OpenAPI 2.2.0**
- **Lombok 1.18.30**
- **JUnit 5**
- **Mockito**

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included Maven wrapper)
- MongoDB 5.0+ (running on localhost:27017)

## Getting Started

### 1. Clone and Navigate
```bash
cd budgeter-backend
```

### 2. Install Dependencies
```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using local Maven installation
mvn clean install
```

### 3. Start MongoDB
Make sure MongoDB is running on localhost:27017

**Option A: Using Docker Compose (Recommended)**
```bash
docker-compose up -d
```

**Option B: Local MongoDB Installation**
Make sure MongoDB is running on localhost:27017

### 4. Run the Application
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using local Maven
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

### 6. Run Tests
```bash
# Using Maven wrapper
./mvnw test

# Or using local Maven
mvn test
```

## Development Setup

### VS Code Configuration
The project includes comprehensive VS Code configuration for Java development:

- **Automatic Formatting**: Code is automatically formatted on save using Google Java Style
- **Import Organization**: Imports are automatically organized
- **Linting**: PMD rules for code quality
- **Debugging**: Pre-configured debug configurations
- **Tasks**: Common Maven operations as VS Code tasks

### Code Formatting
- **EditorConfig**: Consistent formatting across different editors
- **Google Java Style**: Industry-standard Java formatting
- **Auto-format on Save**: Enabled by default
- **Import Organization**: Automatic import sorting and cleanup

### Maven Wrapper
The project includes Maven wrapper scripts for consistent Maven version usage:

- **Windows**: `mvnw.cmd`
- **Unix/Linux/macOS**: `mvnw`
- **Automatic Download**: Maven wrapper JAR is automatically downloaded if missing

### Lombok Integration
Lombok is integrated to reduce boilerplate code:

- **Annotations**: `@Data`, `@Builder`, `@NoArgsConstructor`, etc.
- **Auto-generation**: Getters, setters, constructors, and more
- **IDE Support**: VS Code extension recommended for best experience

## Project Structure

```
src/
├── main/
│   ├── java/com/radomskyi/budgeter/
│   │   ├── BudgeterApplication.java          # Main application class
│   │   ├── config/
│   │   │   ├── MongoConfig.java             # MongoDB configuration
│   │   │   └── OpenApiConfig.java           # OpenAPI configuration
│   │   ├── controller/
│   │   │   └── ExpenseController.java       # REST controller with OpenAPI docs
│   │   ├── domain/
│   │   │   ├── Expense.java                 # Expense entity
│   │   │   ├── Category.java                # Category enum (FIXED, NEEDS, WANTS)
│   │   │   └── Tag.java                     # Tag enum for detailed classification
│   │   ├── dto/
│   │   │   ├── ExpenseRequest.java          # Request DTO with schema docs
│   │   │   └── ExpenseResponse.java         # Response DTO with schema docs
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java  # Global exception handler
│   │   │   └── ErrorResponse.java           # Error response model
│   │   ├── repository/
│   │   │   └── ExpenseRepository.java       # MongoDB repository
│   │   └── service/
│   │       ├── ExpenseService.java          # Service interface
│   │       └── impl/
│   │           └── ExpenseServiceImpl.java  # Service implementation
│   └── resources/
│       └── application.yml                  # Application configuration
├── test/
│   ├── java/com/radomskyi/budgeter/
│   │   ├── controller/
│   │   │   └── ExpenseControllerTest.java   # Controller tests
│   │   ├── service/
│   │   │   └── ExpenseServiceTest.java      # Service tests
│   │   └── integration/
│   │       └── ExpenseIntegrationTest.java  # Integration tests
│   └── resources/
│       └── application-test.yml             # Test configuration
├── .vscode/                                 # VS Code configuration
├── .mvn/                                    # Maven wrapper configuration
├── mvnw                                     # Maven wrapper (Unix)
├── mvnw.cmd                                 # Maven wrapper (Windows)
└── .editorconfig                            # Editor configuration
```

## Architecture

The application follows a **layered architecture** pattern:

- **Controller Layer**: Handles HTTP requests and responses with OpenAPI documentation
- **Service Layer**: Contains business logic and orchestration
- **Repository Layer**: Data access and persistence
- **Domain Layer**: Core business entities and enums (Expense, Category, Tag)
- **DTO Layer**: Data transfer objects for API communication with schema documentation
- **Exception Layer**: Centralized error handling
- **OpenAPI Layer**: API documentation and schema generation

## Configuration

### Application Properties
- **Port**: 8080
- **MongoDB**: localhost:27017/budgeter
- **Logging**: DEBUG level for application and MongoDB operations

### OpenAPI Configuration
- **Swagger UI Path**: /swagger-ui.html
- **OpenAPI JSON Path**: /api-docs
- **API Documentation**: Comprehensive with examples and schemas

### Development Configuration
- **Code Formatting**: Google Java Style
- **Import Organization**: Automatic
- **Linting**: PMD rules enabled
- **Maven Wrapper**: Version 3.9.5

### Test Configuration
- **Test Database**: localhost:27018/budgeter-test
- **Embedded MongoDB**: Used for integration tests

## Testing Strategy

- **Unit Tests**: Service layer with mocked dependencies
- **Controller Tests**: Web layer with MockMvc
- **Integration Tests**: Full application context with embedded MongoDB

## API Examples

### Create Expense
```bash
curl -X POST http://localhost:8080/api/expense \
  -H "Content-Type: application/json" \
  -d '{
    "value": 25.50,
    "category": "NEEDS",
    "description": "Lunch at restaurant",
    "tags": ["FOOD", "BARS_AND_RESTAURANTS"]
  }'
```

### Get All Expenses
```bash
curl http://localhost:8080/api/expenses
```

### Update Expense
```bash
curl -X PUT http://localhost:8080/api/expense/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "value": 30.00,
    "category": "WANTS",
    "description": "Updated lunch with dessert",
    "tags": ["FOOD", "BARS_AND_RESTAURANTS", "ENTERTAINMENT"]
  }'
```

### Delete Expense
```bash
curl -X DELETE http://localhost:8080/api/expense/{id}
```

## Using Swagger UI

1. **Access Swagger UI**: Navigate to http://localhost:8080/swagger-ui.html
2. **Explore Endpoints**: Browse all available API endpoints
3. **Test APIs**: Use the "Try it out" button to test endpoints directly
4. **View Schemas**: Examine request/response models and examples
5. **Download Spec**: Access the OpenAPI specification in JSON or YAML format

## Development Workflow

### Using Maven Wrapper
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package

# Run application
./mvnw spring-boot:run
```

### VS Code Tasks
Use the built-in VS Code tasks for common operations:
- **Maven: Clean** - Clean the project
- **Maven: Compile** - Compile the project
- **Maven: Test** - Run tests
- **Maven: Package** - Package the application
- **Maven: Spring Boot Run** - Run the Spring Boot application

### Code Quality
- **Automatic Formatting**: Code is formatted on save
- **Import Organization**: Imports are automatically organized
- **Linting**: PMD rules help maintain code quality
- **Consistent Style**: Google Java Style ensures consistency

## Extensibility

The architecture is designed for easy extension:

- **New Categories**: Add to the Category enum
- **Additional Tags**: Extend the Tag enum for more detailed classification
- **Additional Fields**: Extend the Expense entity and DTOs
- **New Endpoints**: Add methods to the controller with OpenAPI annotations
- **Business Logic**: Implement in the service layer
- **Custom Queries**: Add methods to the repository
- **API Documentation**: Automatically generated from annotations

## Contributing

1. Follow the existing code structure and patterns
2. Add comprehensive tests for new functionality
3. Include OpenAPI annotations for new endpoints
4. Ensure all tests pass before submitting changes
5. Update documentation as needed
6. Use the provided Maven wrapper for consistent builds
7. Follow the established code formatting standards

## Troubleshooting

### Common Issues

**Maven Wrapper Issues**
- Ensure the wrapper scripts have execute permissions (Unix/Linux/macOS)
- Delete `.mvn/wrapper/maven-wrapper.jar` to force re-download

**VS Code Java Issues**
- Install the recommended extensions
- Ensure Java 17 is properly configured
- Restart VS Code after installing extensions

**Code Formatting Issues**
- Check that the Google Java Style formatter is properly configured
- Ensure the EditorConfig extension is installed

## License

This project is licensed under the MIT License.
