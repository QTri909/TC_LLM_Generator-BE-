# TC_LLM_Generator Backend

Backend REST API xây dựng theo Clean Architecture và Richardson Maturity Model Level 3 (HATEOAS).

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

## Yêu cầu hệ thống

- Java 21 (JDK 21+)
- PostgreSQL 14+
- Maven 3.9+ (hoặc sử dụng Maven Wrapper đi kèm)
- RAM tối thiểu: 2GB
- IDE khuyến nghị: IntelliJ IDEA / VS Code

---

# Setup và chạy project

## Bước 1: Cài đặt Database

### Khởi động PostgreSQL
```bash
sudo service postgresql start
```

### Tạo database và user
```sql
CREATE DATABASE tc_llm_generator;
CREATE USER tc_user WITH PASSWORD 'tc_password123';
GRANT ALL PRIVILEGES ON DATABASE tc_llm_generator TO tc_user;
```

## Bước 2: Cấu hình Application

Tạo các file cấu hình trong `src/main/resources/`:

### File: `application.properties`
- Cấu hình profile active
- Cấu hình tên application

### File: `application-dev.properties`
- Server configuration (port, context-path)
- Database connection (URL, username, password)
- JPA/Hibernate settings (ddl-auto, show-sql, dialect)
- Connection pool settings
- JWT configuration (secret, expiration)
- Logging levels

## Bước 3: Build Project

```bash
# Cấp quyền thực thi cho Maven Wrapper
chmod +x mvnw

# Build project
./mvnw clean install

# Build không chạy test (nhanh hơn)
./mvnw clean install -DskipTests
```

## Bước 4: Chạy Application

### Cách 1: Sử dụng Maven
```bash
./mvnw spring-boot:run
```

### Cách 2: Chạy JAR file
```bash
./mvnw clean package
java -jar target/TC_LLM_Generator-0.0.1-SNAPSHOT.jar
```

### Cách 3: Chạy trong IDE
- Mở project trong IntelliJ IDEA / VS Code
- Tìm class `TcLlmGeneratorApplication.java`
- Click Run hoặc nhấn Shift+F10
- VM options: `-Dspring.profiles.active=dev`

## Bước 5: Kiểm tra Application

```bash
curl http://localhost:8080/actuator/health
```

Application chạy tại: `http://localhost:8080`

---

# Clean Architecture - Kiến trúc Chi tiết

## Tổng quan Clean Architecture

Clean Architecture là kiến trúc phần mềm được thiết kế để:

- ✅ **Độc lập với Framework**: Business logic không phụ thuộc vào Spring, Hibernate
- ✅ **Testable**: Business logic có thể test mà không cần UI, Database, Web Server
- ✅ **Độc lập với UI**: UI có thể thay đổi mà không ảnh hưởng business logic
- ✅ **Độc lập với Database**: Business logic không quan tâm SQL hay NoSQL
- ✅ **Độc lập với External Services**: Business logic không biết về API bên ngoài

## 4 Layers của Clean Architecture

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

### Dependency Rule (Quan trọng nhất!)

```
Infrastructure  ──→  Application  ──→  Domain
Presentation    ──→  Application  ──→  Domain

LUÔN LUÔN: Outer layers → Inner layers
KHÔNG BAO GIỜ: Inner layers → Outer layers
```

**Quy tắc:**
- **Domain Layer**: Không depend vào layer nào (pure Java)
- **Application Layer**: Chỉ depend vào Domain Layer
- **Infrastructure Layer**: Depend vào Application + Domain
- **Presentation Layer**: Depend vào Application + Domain

---

## Cấu trúc thư mục chi tiết

```
src/main/java/com/group05/TC_LLM_Generator/
│
├── 📁 domain/                           # DOMAIN LAYER
│   │                                    # Core business logic - Không depend vào layer nào
│   │
│   ├── 📁 model/
│   │   ├── 📁 entity/                   # Domain Entities (Rich objects)
│   │   │   ├── User.java               # Entity với business logic
│   │   │   ├── Product.java
│   │   │   └── Order.java
│   │   │
│   │   ├── 📁 valueobject/              # Value Objects (Immutable)
│   │   │   ├── Email.java              # Self-validating value objects
│   │   │   ├── Money.java
│   │   │   ├── PhoneNumber.java
│   │   │   └── Address.java
│   │   │
│   │   └── 📁 aggregate/                # Aggregates (Entity clusters)
│   │       ├── OrderAggregate.java     # Nhóm entities liên quan
│   │       └── CartAggregate.java
│   │
│   ├── 📁 service/                      # Domain Services
│   │   ├── PriceCalculationService.java # Business logic phức tạp
│   │   ├── OrderValidationService.java  # Logic không thuộc về 1 entity
│   │   └── UserDomainService.java
│   │
│   ├── 📁 repository/                   # Repository Interfaces (Ports)
│   │   ├── UserRepository.java         # Interface, KHÔNG có implementation
│   │   ├── ProductRepository.java
│   │   └── OrderRepository.java
│   │
│   ├── 📁 exception/                    # Domain Exceptions
│   │   ├── UserAlreadyExistsException.java
│   │   ├── InsufficientStockException.java
│   │   └── InvalidEmailException.java
│   │
│   └── 📁 event/                        # Domain Events
│       ├── UserRegisteredEvent.java
│       └── OrderPlacedEvent.java
│
├── 📁 application/                      # APPLICATION LAYER
│   │                                    # Use Cases - Orchestrates domain logic
│   │
│   ├── 📁 port/
│   │   ├── 📁 in/                       # Input Ports (Use Case Interfaces)
│   │   │   ├── CreateUserUseCase.java  # Interface cho use case
│   │   │   ├── GetUserUseCase.java
│   │   │   ├── UpdateUserUseCase.java
│   │   │   ├── DeleteUserUseCase.java
│   │   │   └── AuthenticateUserUseCase.java
│   │   │
│   │   └── 📁 out/                      # Output Ports (Interfaces)
│   │       ├── LoadUserPort.java       # Interface để load user
│   │       ├── SaveUserPort.java       # Interface để save user
│   │       └── SendEmailPort.java      # Interface để gửi email
│   │
│   ├── 📁 service/                      # Application Services (Use Case Implementation)
│   │   ├── UserService.java            # Implements các Use Case interfaces
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   └── AuthService.java
│   │
│   ├── 📁 dto/                          # Application DTOs (Internal)
│   │   ├── UserCommand.java            # Commands (input)
│   │   ├── UserQuery.java              # Queries (input)
│   │   └── UserDto.java                # Data transfer objects
│   │
│   └── 📁 mapper/                       # Mappers (Domain ↔ Application DTO)
│       ├── UserApplicationMapper.java
│       └── ProductApplicationMapper.java
│
├── 📁 infrastructure/                   # INFRASTRUCTURE LAYER
│   │                                    # Technical implementation details
│   │
│   ├── 📁 persistence/
│   │   ├── 📁 entity/                   # JPA Entities (Database schema)
│   │   │   ├── UserJpaEntity.java      # @Entity, @Table annotations
│   │   │   ├── ProductJpaEntity.java
│   │   │   └── OrderJpaEntity.java
│   │   │
│   │   ├── 📁 repository/               # JPA Repository Implementation
│   │   │   ├── UserJpaRepository.java  # extends JpaRepository
│   │   │   ├── UserRepositoryAdapter.java # Implements Domain UserRepository
│   │   │   ├── ProductJpaRepository.java
│   │   │   └── ProductRepositoryAdapter.java
│   │   │
│   │   └── 📁 mapper/                   # Mappers (Domain ↔ JPA Entity)
│   │       ├── UserPersistenceMapper.java
│   │       └── ProductPersistenceMapper.java
│   │
│   ├── 📁 security/                     # Security Implementation
│   │   ├── SecurityConfig.java         # Spring Security configuration
│   │   ├── JwtTokenProvider.java       # JWT token generation/validation
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   │
│   ├── 📁 external/                     # External Services
│   │   ├── EmailServiceAdapter.java    # Implements SendEmailPort
│   │   ├── PaymentServiceAdapter.java
│   │   └── StorageServiceAdapter.java
│   │
│   └── 📁 config/                       # Infrastructure Configurations
│       ├── DatabaseConfig.java
│       ├── JpaConfig.java
│       └── RestTemplateConfig.java
│
└── 📁 presentation/                     # PRESENTATION LAYER
    │                                    # REST API - HTTP interface
    │
    ├── 📁 controller/                   # REST Controllers
    │   ├── UserController.java         # @RestController
    │   ├── ProductController.java
    │   ├── OrderController.java
    │   └── AuthController.java
    │
    ├── 📁 dto/                          # API DTOs (External)
    │   ├── 📁 request/                  # Request DTOs
    │   │   ├── CreateUserRequest.java  # @Valid, validation annotations
    │   │   ├── UpdateUserRequest.java
    │   │   └── LoginRequest.java
    │   │
    │   └── 📁 response/                 # Response DTOs
    │       ├── UserResponse.java       # DTO trả về client
    │       ├── ProductResponse.java
    │       └── ApiResponse.java        # Wrapper response
    │
    ├── 📁 mapper/                       # Mappers (Application ↔ API DTO)
    │   ├── UserPresentationMapper.java
    │   └── ProductPresentationMapper.java
    │
    ├── 📁 assembler/                    # HATEOAS Assemblers
    │   ├── UserModelAssembler.java     # Add HATEOAS links
    │   └── ProductModelAssembler.java
    │
    └── 📁 exception/                    # Exception Handlers
        ├── GlobalExceptionHandler.java # @RestControllerAdvice
        ├── ApiError.java
        └── ErrorResponse.java
```

---

## Giải thích chi tiết từng Layer

### 1. Domain Layer (Lõi của hệ thống)

**Mục đích:** Chứa toàn bộ business logic và business rules

**Đặc điểm:**
- Pure Java, không có framework annotations
- Không depend vào layer nào khác
- Entities là "Rich Domain Models" (có behavior, không chỉ là data)
- Value Objects là immutable và self-validating

**Các thành phần:**

| Component | Mô tả | Ví dụ |
|-----------|-------|-------|
| **Entity** | Rich objects với business logic | User.activate(), Order.cancel() |
| **Value Object** | Immutable objects đại diện cho values | Email, Money, Address |
| **Aggregate** | Nhóm entities liên quan với consistency boundary | OrderAggregate (Order + OrderItems) |
| **Domain Service** | Business logic không thuộc về 1 entity | PriceCalculationService |
| **Repository Interface** | Contract để persist/retrieve entities | UserRepository (interface only) |
| **Domain Exception** | Business rule violations | InsufficientStockException |
| **Domain Event** | Sự kiện business logic | OrderPlacedEvent |

**Ví dụ Entity:** User có methods như `activate()`, `deactivate()`, `changePassword()` chứ không chỉ là getters/setters

**Ví dụ Value Object:** Email tự validate format, Money tự tính toán với currency

### 2. Application Layer (Use Cases)

**Mục đích:** Orchestrates business logic, implements use cases

**Đặc điểm:**
- Định nghĩa các use cases của application
- Gọi domain logic nhưng không chứa business rules
- Transaction boundaries
- Coordination giữa nhiều domain objects

**Các thành phần:**

| Component | Mô tả | Vai trò |
|-----------|-------|---------|
| **Input Port** | Use case interface | Định nghĩa what application có thể làm |
| **Output Port** | Repository/Service interface | Định nghĩa what application cần từ infrastructure |
| **Application Service** | Implementation của use cases | Orchestrates domain logic |
| **DTO** | Data transfer objects | Truyền data giữa layers |
| **Mapper** | Convert giữa Domain ↔ DTO | Tách biệt domain và external representation |

**Flow:** Controller → Use Case Interface → Application Service → Domain Logic → Repository Interface

### 3. Infrastructure Layer (Technical Details)

**Mục đích:** Implementation của technical concerns

**Đặc điểm:**
- Chứa framework-specific code
- Implements interfaces từ Domain/Application
- Database, Security, External APIs
- Configuration

**Các thành phần:**

| Component | Mô tả | Ví dụ |
|-----------|-------|-------|
| **JPA Entity** | Database schema representation | @Entity, @Table annotations |
| **JPA Repository** | Spring Data JPA repository | extends JpaRepository |
| **Repository Adapter** | Implements Domain Repository | Chuyển đổi JPA Entity ↔ Domain Entity |
| **Security Config** | Spring Security configuration | JWT, authentication, authorization |
| **External Adapter** | Gọi external services | EmailServiceAdapter, PaymentAdapter |
| **Configuration** | Infrastructure configs | Database, Security, External APIs |

**Pattern:** Adapter Pattern - Adapters implement domain interfaces và delegate to framework-specific code

### 4. Presentation Layer (REST API)

**Mục đích:** HTTP interface, entry point cho requests

**Đặc điểm:**
- REST Controllers
- Request/Response DTOs
- HATEOAS links
- Exception handling
- Validation

**Các thành phần:**

| Component | Mô tả | Vai trò |
|-----------|-------|---------|
| **Controller** | REST endpoints | Nhận HTTP requests, gọi use cases |
| **Request DTO** | Input validation | @Valid, @NotNull, @Email... |
| **Response DTO** | Output format | JSON representation cho client |
| **HATEOAS Assembler** | Add hypermedia links | Level 3 REST API |
| **Exception Handler** | Centralized error handling | @RestControllerAdvice |
| **Mapper** | Convert Use Case DTO ↔ API DTO | Tách biệt internal/external DTOs |

**Flow:** HTTP Request → Controller → Validate → Map to Command → Call Use Case → Map to Response → Add HATEOAS links → Return JSON

---

## Flow của một Request (Chi tiết từng bước)

### Ví dụ: GET /api/v1/users/123

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP GET /api/v1/users/123
       ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. PRESENTATION LAYER - UserController                      │
│    - Nhận HTTP request                                      │
│    - Extract path variable (id = 123)                       │
│    - Gọi Use Case Interface                                 │
└───────────────────────────┬─────────────────────────────────┘
                            │ getUserById(123)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. APPLICATION LAYER - UserService (implements UseCase)     │
│    - Nhận request từ Controller                             │
│    - Validate input (nếu cần)                               │
│    - Gọi Domain Repository Interface                        │
└───────────────────────────┬─────────────────────────────────┘
                            │ findById(123)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. DOMAIN LAYER - UserRepository (Interface)                │
│    - Định nghĩa contract: Optional<User> findById(Long id) │
└───────────────────────────┬─────────────────────────────────┘
                            │ (implementation in Infrastructure)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. INFRASTRUCTURE LAYER - UserRepositoryAdapter             │
│    - Implements Domain UserRepository interface             │
│    - Gọi UserJpaRepository (Spring Data)                    │
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
│   Client    │ Nhận JSON response với hypermedia links
└─────────────┘
```

### Key Points của Flow:

1. **Request đi từ ngoài vào trong:** Presentation → Application → Domain
2. **Response đi từ trong ra ngoài:** Domain → Application → Presentation
3. **Mỗi layer có DTOs riêng:**
   - Domain: Domain Entities (User)
   - Application: Application DTOs (UserDto)
   - Presentation: API DTOs (UserResponse)
4. **Mappers ở mỗi boundary:** Convert data giữa các layers
5. **Dependency luôn hướng vào trong:** Outer layers depend on inner layers

---

## Testing trong Clean Architecture

### Cấu trúc Test

```
src/test/java/com/group05/TC_LLM_Generator/
├── domain/
│   ├── UserTest.java                # Unit test cho Domain Entity
│   └── EmailTest.java               # Unit test cho Value Object
│
├── application/
│   └── UserServiceTest.java         # Unit test cho Use Case (mock repository)
│
├── infrastructure/
│   └── UserRepositoryAdapterTest.java  # Integration test với database
│
└── presentation/
    └── UserControllerTest.java      # Integration test cho REST API
```

### Testing Strategy

| Layer | Test Type | Mock gì? | Test gì? |
|-------|-----------|----------|----------|
| **Domain** | Unit Test | Không mock | Business logic, validation |
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

## Lợi ích của Clean Architecture

### ✅ Testability
- Mỗi layer test độc lập
- Domain layer test không cần framework
- Use case test với mocked repositories

### ✅ Maintainability
- Thay đổi UI không ảnh hưởng business logic
- Thay đổi database không ảnh hưởng use cases
- Code dễ đọc, dễ hiểu với clear separation

### ✅ Flexibility
- Swap PostgreSQL → MongoDB chỉ cần thay Infrastructure layer
- Thay REST API → GraphQL chỉ cần thay Presentation layer
- Thay Spring → Quarkus không ảnh hưởng Domain logic

### ✅ Independence
- Business logic hoàn toàn độc lập
- Có thể test business logic mà không cần Spring, Database
- Domain layer là pure Java

### ✅ Team Collaboration
- Frontend team làm việc với Presentation layer
- Backend team làm việc với Application/Domain
- DBA team làm việc với Infrastructure persistence
- Các team có thể làm việc song song

---

## Lưu ý khi áp dụng Clean Architecture

### ⚠️ Trade-offs

**Ưu điểm:**
- Code quality cao
- Dễ maintain và extend
- Testability tốt
- Independence giữa các layers

**Nhược điểm:**
- Nhiều boilerplate code (mappers, adapters...)
- Learning curve cao
- Initial setup phức tạp
- Overhead cho small projects

### 📚 Khi nào nên dùng Clean Architecture?

✅ **NÊN dùng khi:**
- Project lớn, phức tạp
- Cần maintain lâu dài (> 2 năm)
- Team nhiều người
- Business logic phức tạp
- Yêu cầu high testability

❌ **KHÔNG NÊN dùng khi:**
- Project nhỏ, đơn giản
- Prototype, POC
- Deadline gấp
- Team ít kinh nghiệm
- CRUD đơn giản

### 💡 Best Practices

1. **Start simple, refactor later** - Không cần perfect từ đầu
2. **Consistent naming** - Đặt tên rõ ràng cho từng layer
3. **Clear boundaries** - Tách biệt layers với packages
4. **Use Mappers** - Không expose internal structure
5. **Test each layer** - Unit test domain, integration test infrastructure
6. **Document flow** - Giải thích data flow giữa layers

---

## Các lệnh hữu ích

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

| Profile | Mục đích | Config File |
|---------|----------|-------------|
| `dev` | Development | `application-dev.properties` |
| `test` | Testing | `application-test.properties` |
| `prod` | Production | `application-prod.properties` |

```bash
# Chuyển profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

**Last Updated:** 2026-02-03
**Version:** 0.0.1-SNAPSHOT
**Team:** Group 05 - SWD391
