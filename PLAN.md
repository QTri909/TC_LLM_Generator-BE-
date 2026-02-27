# Merge Plan: feature/authentica → refact/merge-dev-authen

## Status: Code Changes COMPLETE — Build Verification Remaining

All code has been written. SDKMAN session needs to be initialized before building.

---

## What Was Done (Completed)

### 1. pom.xml — Added dependencies
- `com.google.api-client:google-api-client:2.8.1` (Google OAuth token verification)
- `me.paulschwarz:spring-dotenv:4.0.0` (.env file support)

### 2. UserEntity updated
- `passwordHash` → nullable (for OAuth users who have no password)
- Added `authProvider` field (LOCAL / GOOGLE)
- Added `lastActiveWorkspaceId` field
- File: `infrastructure/persistence/entity/UserEntity.java`

### 3. Domain layer created (NEW)
- `domain/model/entity/User.java` — pure POJO domain model
- `domain/repository/UserRepo.java` — domain repository interface

### 4. Infrastructure persistence mapper + adapter (NEW)
- `infrastructure/persistence/mapper/UserMapper.java` — MapStruct mapper (domain User ↔ JPA UserEntity)
- `infrastructure/persistence/adapter/UserRepoAdapter.java` — implements `UserRepo` using `UserMapper`
- NOTE: Existing `UserRepositoryAdapter` (for CRUD) left untouched — both coexist

### 5. Auth application ports (NEW)
- `application/port/in/authen/LoginUseCase.java`
- `application/port/in/authen/LoginWithPasswordUseCase.java`
- `application/port/in/authen/RegisterUseCase.java`
- `application/port/in/authen/dto/request/LoginRequest.java`
- `application/port/in/authen/dto/request/RegisterRequest.java`
- `application/port/in/authen/dto/result/AuthResponse.java`
- `application/port/out/authen/VerifyTokenPort.java`
- `application/port/out/authen/dto/info/GoogleUserInfo.java`

### 6. Auth services (NEW)
- `application/service/authen/LoginService.java` — Google OAuth login (verify token → find/create user → JWT)
- `application/service/authen/LoginWithPasswordService.java` — email+password → AuthenticationManager → JWT
- `application/service/authen/RegisterService.java` — register with BCrypt → JWT

### 7. Security infrastructure (NEW)
- `infrastructure/security/JwtTokenProvider.java` — HS512 token generation (access 1h, refresh 7d)
- `infrastructure/security/CustomJwtDecoder.java` — validates local JWT, skips Google `ya29.` tokens
- `infrastructure/security/CustomUserDetails.java` — Spring Security UserDetails wrapper
- `infrastructure/security/CustomUserDetailsService.java` — loads user by email via UserRepo
- `infrastructure/security/JwtAuthenticationEntryPoint.java` — returns ProblemDetail JSON on 401

### 8. SecurityConfig replaced
- DELETED: `infrastructure/config/SecurityConfig.java` (old permit-all)
- CREATED: `infrastructure/security/SecurityConfig.java` (JWT-enabled)
  - Public endpoints: `/api/v1/auth/**`, `/users/**`, Swagger
  - All other requests: `.authenticated()`
  - OAuth2 resource server with CustomJwtDecoder
  - BCryptPasswordEncoder + AuthenticationManager beans
  - CORS: localhost:3000/5173/4200/8080

### 9. Google OAuth adapter (NEW)
- `infrastructure/external/authen/VerifierAdapter.java` — verifies Google ID tokens

### 10. AuthenController (NEW)
- `presentation/controller/AuthenController.java`
  - `POST /api/v1/auth/login-google` — Google OAuth login
  - `POST /api/v1/auth/login` — email/password login
  - `POST /api/v1/auth/register` — registration

### 11. Config files
- CREATED: `src/main/resources/application-dev.yml` (jwt.secret, jwt.expiration-*, google.client-id)
- UPDATED: `application.properties` → `spring.profiles.active=postgres,dev`

---

## What Remains

### Step 1: Build & Fix Errors
```bash
# Initialize SDKMAN first
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Then build
./mvnw clean install -DskipTests
```

**Potential issues to watch for:**
- Jackson namespace: The project uses `com.fasterxml.jackson` (Jackson 2.x). If Spring Boot 4.0.2 ships Jackson 3.x (`tools.jackson`), `JwtAuthenticationEntryPoint.java` import may need changing from `com.fasterxml.jackson.databind.ObjectMapper` to `tools.jackson.databind.ObjectMapper`
- MapStruct compilation: `UserMapper` maps between domain `User` and JPA `UserEntity` with field name differences — verify generated mapper is correct
- Any missing imports or Spring bean conflicts

### Step 2: Update CLAUDE.md
Update `backend/CLAUDE.md` to document:
- New auth system (JWT + Google OAuth)
- New domain layer
- Auth endpoints (`/api/v1/auth/login`, `/register`, `/login-google`)
- `application-dev.yml` config (jwt, google)
- Security: no longer permit-all, JWT enforced
- New packages: `infrastructure/security/`, `infrastructure/external/`, `domain/`, `application/port/in/authen/`, `application/service/authen/`

---

## Architecture After Merge

```
Presentation (controllers, DTOs, HATEOAS assemblers, mappers)
  ├── AuthenController        ← NEW (auth endpoints)
  ├── UserController          ← existing (CRUD)
  ├── WorkspaceController     ← existing (CRUD)
  ├── ProjectController       ← existing (CRUD)
  ├── TestCaseController      ← existing (CRUD)
  └── TestPlanController      ← existing (CRUD)

Application (services, ports)
  ├── service/authen/         ← NEW (Login, Register, LoginWithPassword)
  ├── service/                ← existing (User, Workspace, Project, TestCase, TestPlan)
  ├── port/in/authen/         ← NEW (use case interfaces + DTOs)
  └── port/out/               ← existing ports + NEW authen/VerifyTokenPort

Domain (pure models, no framework deps)
  ├── model/entity/User.java  ← NEW (domain model for auth)
  └── repository/UserRepo.java ← NEW (domain repo interface for auth)

Infrastructure
  ├── security/               ← NEW (SecurityConfig, JWT, UserDetails)
  ├── external/authen/        ← NEW (Google VerifierAdapter)
  ├── persistence/mapper/     ← NEW (UserMapper: domain ↔ JPA)
  ├── persistence/adapter/    ← existing + NEW UserRepoAdapter
  ├── persistence/entity/     ← existing (UserEntity updated with authProvider)
  └── config/OpenApiConfig    ← existing (SecurityConfig moved to security/)
```
