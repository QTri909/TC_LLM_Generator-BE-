# TC_LLM_Generator Backend

Backend REST API built with Clean Architecture and Richardson Maturity Model Level 3 (HATEOAS).

## Tech Stack

- **Spring Boot**: 4.0.2
- **Java**: 21
- **Database**: PostgreSQL
- **Security**: Spring Security + OAuth2 JWT
- **ORM**: Spring Data JPA + Hibernate
- **API**: Spring HATEOAS (Level 3 REST)
- **Validation**: Spring Validation
- **Build Tool**: Maven
- **Code Generation**: Lombok

## System Requirements

- Java 21 (JDK 21+)
- PostgreSQL 14+
- Maven 3.9+ (or use included Maven Wrapper)
- Minimum RAM: 2GB
- Recommended IDE: IntelliJ IDEA / VS Code

---

# Setup and Run

## Step 1: Setup Git Hooks with Husky

This project uses Husky to enforce commit message conventions and code quality standards.

### Install Git Hooks

After cloning the repository, run the following command to set up Git hooks:

#### All commands line must run in root folder project.

```bash
# Install dependencies (if using npm-based Husky)
npm install

# Check if husky existed or not
which husky #on MacOS and Linux

where husky #on Window

# install husky to project
husky install 
```

### What Husky Does

- **Pre-commit hooks**: Validates code quality before commits
- **Commit message validation**: Enforces conventional commit message format
- **Pre-push hooks**: Runs tests before pushing to remote repository

### Commit Message Format

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <subject>

Examples:
feat: add user authentication endpoint
fix: resolve null pointer exception in UserService
docs: update README with Husky setup instructions
refactor: restructure domain layer packages
test: add unit tests for UserRepository
```

**Common types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes
- `style`: Code style changes (formatting, missing semi-colons, etc.)

## Step 3: Application Configuration

Create configuration files in `src/main/resources/`:

### File: `application.properties`
- Configure active profile
- Configure application name

### File: `application-dev.properties`
- Server configuration (port, context-path)
- Database connection (URL, username, password)
- JPA/Hibernate settings (ddl-auto, show-sql, dialect)
- Connection pool settings
- JWT configuration (secret, expiration)
- Logging levels

## Step 4: Build Project

```bash
# Grant execute permission for Maven Wrapper
chmod +x mvnw

# Build project
./mvnw clean install

# Build without tests (faster)
./mvnw clean install -DskipTests
```

## Step 5: Run Application

### Method 1: Using Maven
```bash
./mvnw spring-boot:run
```

### Method 2: Run JAR file
```bash
./mvnw clean package
java -jar target/TC_LLM_Generator-0.0.1-SNAPSHOT.jar
```

### Method 3: Run in IDE
- Open project in IntelliJ IDEA / VS Code
- Find class `TcLlmGeneratorApplication.java`
- Click Run or press Shift+F10
- VM options: `-Dspring.profiles.active=dev`

## Step 6: Verify Application

```bash
curl http://localhost:8080/actuator/health
```

Application runs at: `http://localhost:8080`

---

# Clean Architecture - Detailed Architecture

## Clean Architecture Overview

Clean Architecture is a software architecture designed to achieve:

- ✅ **Framework Independence**: Business logic doesn't depend on Spring, Hibernate
- ✅ **Testable**: Business logic can be tested without UI, Database, Web Server
- ✅ **UI Independence**: UI can change without affecting business logic
- ✅ **Database Independence**: Business logic doesn't care about SQL or NoSQL
- ✅ **External Services Independence**: Business logic doesn't know about external APIs

## 4 Layers of Clean Architecture

```
┌───────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                         │
│          Controllers, DTOs, Exception Handlers                 │
│              REST API + HATEOAS + Validation                   │
│                    [Framework & Drivers]                       │
└────────────────────────────┬──────────────────────────────────┘
                             │ calls
                             ↓
┌───────────────────────────────────────────────────────────────┐
│                     APPLICATION LAYER                          │
│        Use Cases, Application Services, Input/Output Ports     │
│              Orchestrates Domain Logic                         │
│                    [Interface Adapters]                        │
└────────────────────────────┬──────────────────────────────────┘
                             │ uses
                             ↓
┌───────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                             │
│    Entities, Value Objects, Domain Services, Business Rules    │
│              Pure Business Logic (No dependencies)             │
│                      [Enterprise Business Rules]               │
└────────────────────────────┬──────────────────────────────────┘
                             ↑ implements
                             │
┌───────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                         │
│     JPA Repositories, Security, External APIs, Configs         │
│              Technical Implementation Details                  │
│                    [Framework & Drivers]                       │
└───────────────────────────────────────────────────────────────┘
```

### Dependency Rule (Most Important!)

```
Infrastructure  ──→  Application  ──→  Domain
Presentation    ──→  Application  ──→  Domain

ALWAYS: Outer layers → Inner layers
NEVER: Inner layers → Outer layers
```

**Rules:**
- **Domain Layer**: No dependencies on any layer (pure Java)
- **Application Layer**: Only depends on Domain Layer
- **Infrastructure Layer**: Depends on Application + Domain
- **Presentation Layer**: Depends on Application + Domain

---

## Detailed Directory Structure

```
src/main/java/com/group05/TC_LLM_Generator/
│
├── 📁 domain/                           # DOMAIN LAYER - Core business logic (no external dependencies)
│   ├── 📁 model/
│   │   ├── 📁 entity/                   # Domain Entities with business logic
│   │   ├── 📁 valueobject/              # Immutable value objects with self-validation
│   │   └── 📁 aggregate/                # Entity aggregates with consistency boundaries
│   ├── 📁 service/                      # Domain services for complex business logic
│   ├── 📁 repository/                   # Repository interfaces (ports, no implementation)
│   ├── 📁 exception/                    # Domain-specific business exceptions
│   └── 📁 event/                        # Domain events for business occurrences
│
├── 📁 application/                      # APPLICATION LAYER - Use cases orchestrating domain logic
│   ├── 📁 port/
│   │   ├── 📁 in/                       # Input ports (use case interfaces)
│   │   └── 📁 out/                      # Output ports (repository/service interfaces)
│   ├── 📁 service/                      # Application services implementing use cases
│   ├── 📁 dto/                          # Internal application DTOs (commands, queries)
│   └── 📁 mapper/                       # Mappers between domain and application DTOs
│
├── 📁 infrastructure/                   # INFRASTRUCTURE LAYER - Technical implementation
│   ├── 📁 persistence/
│   │   ├── 📁 entity/                   # JPA entities with database annotations
│   │   ├── 📁 repository/               # JPA repositories and adapters
│   │   └── 📁 mapper/                   # Mappers between domain and JPA entities
│   ├── 📁 security/                     # Spring Security, JWT, authentication filters
│   ├── 📁 external/                     # External service adapters (email, payment, storage)
│   └── 📁 config/                       # Infrastructure configurations (database, JPA, etc.)
│
└── 📁 presentation/                     # PRESENTATION LAYER - REST API HTTP interface
    ├── 📁 controller/                   # REST controllers with @RestController
    ├── 📁 dto/
    │   ├── 📁 request/                  # Request DTOs with validation annotations
    │   └── 📁 response/                 # Response DTOs for client output
    ├── 📁 mapper/                       # Mappers between application and API DTOs
    ├── 📁 assembler/                    # HATEOAS assemblers for hypermedia links
    └── 📁 exception/                    # Global exception handlers with @RestControllerAdvice
```

---

## Detailed Layer Explanation

### 1. Domain Layer (System Core)

**Purpose:** Contains all business logic and business rules

**Characteristics:**
- Pure Java, no framework annotations
- No dependencies on any other layer
- Entities are "Rich Domain Models" (with behavior, not just data)
- Value Objects are immutable and self-validating

**Components:**

| Component | Description | Example |
|-----------|-------------|---------|
| **Entity** | Rich objects with business logic | User.activate(), Order.cancel() |
| **Value Object** | Immutable objects representing values | Email, Money, Address |
| **Aggregate** | Related entities with consistency boundary | OrderAggregate (Order + OrderItems) |
| **Domain Service** | Business logic not belonging to one entity | PriceCalculationService |
| **Repository Interface** | Contract to persist/retrieve entities | UserRepository (interface only) |
| **Domain Exception** | Business rule violations | InsufficientStockException |
| **Domain Event** | Business logic events | OrderPlacedEvent |

**Entity Example:** User has methods like `activate()`, `deactivate()`, `changePassword()`, not just getters/setters

**Value Object Example:** Email self-validates format, Money self-calculates with currency

### 2. Application Layer (Use Cases)

**Purpose:** Orchestrates business logic, implements use cases

**Characteristics:**
- Defines application use cases
- Calls domain logic but doesn't contain business rules
- Transaction boundaries
- Coordination between multiple domain objects

**Components:**

| Component | Description | Role |
|-----------|-------------|------|
| **Input Port** | Use case interface | Defines what application can do |
| **Output Port** | Repository/Service interface | Defines what application needs from infrastructure |
| **Application Service** | Use case implementation | Orchestrates domain logic |
| **DTO** | Data transfer objects | Transfer data between layers |
| **Mapper** | Convert Domain ↔ DTO | Separate domain from external representation |

**Flow:** Controller → Use Case Interface → Application Service → Domain Logic → Repository Interface

### 3. Infrastructure Layer (Technical Details)

**Purpose:** Implementation of technical concerns

**Characteristics:**
- Contains framework-specific code
- Implements interfaces from Domain/Application
- Database, Security, External APIs
- Configuration

**Components:**

| Component | Description | Example |
|-----------|-------------|---------|
| **JPA Entity** | Database schema representation | @Entity, @Table annotations |
| **JPA Repository** | Spring Data JPA repository | extends JpaRepository |
| **Repository Adapter** | Implements Domain Repository | Converts JPA Entity ↔ Domain Entity |
| **Security Config** | Spring Security configuration | JWT, authentication, authorization |
| **External Adapter** | Call external services | EmailServiceAdapter, PaymentAdapter |
| **Configuration** | Infrastructure configs | Database, Security, External APIs |

**Pattern:** Adapter Pattern - Adapters implement domain interfaces and delegate to framework-specific code

### 4. Presentation Layer (REST API)

**Purpose:** HTTP interface, entry point for requests

**Characteristics:**
- REST Controllers
- Request/Response DTOs
- HATEOAS links
- Exception handling
- Validation

**Components:**

| Component | Description | Role |
|-----------|-------------|------|
| **Controller** | REST endpoints | Receive HTTP requests, call use cases |
| **Request DTO** | Input validation | @Valid, @NotNull, @Email... |
| **Response DTO** | Output format | JSON representation for client |
| **HATEOAS Assembler** | Add hypermedia links | Level 3 REST API |
| **Exception Handler** | Centralized error handling | @RestControllerAdvice |
| **Mapper** | Convert Use Case DTO ↔ API DTO | Separate internal/external DTOs |

**Flow:** HTTP Request → Controller → Validate → Map to Command → Call Use Case → Map to Response → Add HATEOAS links → Return JSON

---

## Request Flow (Detailed Step-by-Step)

### Example: GET /api/v1/users/123

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP GET /api/v1/users/123
       ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. PRESENTATION LAYER - UserController                      │
│    - Receive HTTP request                                   │
│    - Extract path variable (id = 123)                       │
│    - Call Use Case Interface                                │
└───────────────────────────┬─────────────────────────────────┘
                            │ getUserById(123)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. APPLICATION LAYER - UserService (implements UseCase)     │
│    - Receive request from Controller                        │
│    - Validate input (if needed)                             │
│    - Call Domain Repository Interface                       │
└───────────────────────────┬─────────────────────────────────┘
                            │ findById(123)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. DOMAIN LAYER - UserRepository (Interface)                │
│    - Define contract: Optional<User> findById(Long id)      │
└───────────────────────────┬─────────────────────────────────┘
                            │ (implementation in Infrastructure)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. INFRASTRUCTURE LAYER - UserRepositoryAdapter             │
│    - Implements Domain UserRepository interface             │
│    - Call UserJpaRepository (Spring Data)                   │
│    - Map JPA Entity → Domain Entity                         │
└───────────────────────────┬─────────────────────────────────┘
                            │ Query database
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. DATABASE - PostgreSQL                                    │
│    - SELECT * FROM users WHERE id = 123                     │
│    - Return UserJpaEntity                                   │
└───────────────────────────┬─────────────────────────────────┘
                            │ UserJpaEntity
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. INFRASTRUCTURE → APPLICATION                              │
│    - Mapper: UserJpaEntity → Domain User                    │
│    - Return Domain User object                              │
└───────────────────────────┬─────────────────────────────────┘
                            │ Domain User
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. APPLICATION → PRESENTATION                                │
│    - Mapper: Domain User → UserDto                          │
│    - Return UserDto to Controller                           │
└───────────────────────────┬─────────────────────────────────┘
                            │ UserDto
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. PRESENTATION LAYER - UserController                      │
│    - Mapper: UserDto → UserResponse                         │
│    - Add HATEOAS links (self, update, delete...)            │
│    - Wrap in ApiResponse<EntityModel<UserResponse>>         │
│    - Return ResponseEntity with HTTP 200                    │
└───────────────────────────┬─────────────────────────────────┘
                            │ JSON with HATEOAS links
                            ↓
┌─────────────┐
│   Client    │ Receive JSON response with hypermedia links
└─────────────┘
```

### Key Points of Flow:

1. **Request flows from outside to inside:** Presentation → Application → Domain
2. **Response flows from inside to outside:** Domain → Application → Presentation
3. **Each layer has its own DTOs:**
   - Domain: Domain Entities (User)
   - Application: Application DTOs (UserDto)
   - Presentation: API DTOs (UserResponse)
4. **Mappers at each boundary:** Convert data between layers
5. **Dependencies always point inward:** Outer layers depend on inner layers

---

## Testing in Clean Architecture

### Test Structure

```
src/test/java/com/group05/TC_LLM_Generator/
├── domain/                          # Unit tests for domain entities and value objects
├── application/                     # Unit tests for use cases (with mocked repositories)
├── infrastructure/                  # Integration tests with database
└── presentation/                    # Integration tests for REST API endpoints
```

### Testing Strategy

| Layer | Test Type | What to Mock? | What to Test? |
|-------|-----------|---------------|---------------|
| **Domain** | Unit Test | Nothing | Business logic, validation |
| **Application** | Unit Test | Mock Repository | Use case logic, orchestration |
| **Infrastructure** | Integration Test | Database testcontainer | Database operations |
| **Presentation** | Integration Test | Mock Use Cases | HTTP endpoints, validation, HATEOAS |

### Test Pyramid

```
                    ▲
                   / \
                  /   \
                 /  E2E \
                /       \
               /---------\
              /           \
             / Integration \
            /               \
           /-----------------\
          /                   \
         /     Unit Tests      \
        /                       \
       /-------------------------\
```

---

## Benefits of Clean Architecture

### ✅ Testability
- Each layer tested independently
- Domain layer tested without framework
- Use case tested with mocked repositories

### ✅ Maintainability
- UI changes don't affect business logic
- Database changes don't affect use cases
- Code is readable with clear separation

### ✅ Flexibility
- Swap PostgreSQL → MongoDB by changing Infrastructure layer only
- Change REST API → GraphQL by changing Presentation layer only
- Change Spring → Quarkus without affecting Domain logic

### ✅ Independence
- Business logic completely independent
- Test business logic without Spring, Database
- Domain layer is pure Java

### ✅ Team Collaboration
- Frontend team works on Presentation layer
- Backend team works on Application/Domain
- DBA team works on Infrastructure persistence
- Teams can work in parallel

---

## Clean Architecture Considerations

### ⚠️ Trade-offs

**Advantages:**
- High code quality
- Easy to maintain and extend
- Good testability
- Independence between layers

**Disadvantages:**
- More boilerplate code (mappers, adapters...)
- High learning curve
- Complex initial setup
- Overhead for small projects

### 📚 When to Use Clean Architecture?

✅ **USE when:**
- Large, complex project
- Long-term maintenance (> 2 years)
- Large team
- Complex business logic
- High testability required

❌ **DON'T USE when:**
- Small, simple project
- Prototype, POC
- Tight deadline
- Inexperienced team
- Simple CRUD operations

### 💡 Best Practices

1. **Start simple, refactor later** - No need to be perfect from start
2. **Consistent naming** - Clear names for each layer
3. **Clear boundaries** - Separate layers with packages
4. **Use Mappers** - Don't expose internal structure
5. **Test each layer** - Unit test domain, integration test infrastructure
6. **Document flow** - Explain data flow between layers

---

## Useful Commands

### Maven Commands

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run

# Test
./mvnw test

# Package
./mvnw package -DskipTests

# Verify (with coverage)
./mvnw verify

# Dependency tree
./mvnw dependency:tree
```

### Profiles

| Profile | Purpose | Config File |
|---------|---------|-------------|
| `dev` | Development | `application-dev.properties` |
| `test` | Testing | `application-test.properties` |
| `prod` | Production | `application-prod.properties` |

```bash
# Switch profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

**Last Updated:** 2026-02-03
**Version:** 0.0.1-SNAPSHOT
**Team:** Group 05 - SWD391
