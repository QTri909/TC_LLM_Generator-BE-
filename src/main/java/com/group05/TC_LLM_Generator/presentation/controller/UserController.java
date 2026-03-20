package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.domain.model.enums.Role;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.presentation.assembler.UserModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateUserRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateUserRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStatsResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.UserPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for User CRUD operations.
 * Implements HATEOAS Level 3 REST API with wrapped responses and pagination.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPresentationMapper mapper;
    private final UserModelAssembler assembler;
    private final PagedResourcesAssembler<UserEntity> pagedResourcesAssembler;

    /**
     * Create a new user (admin only)
     * POST /api/v1/users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserEntity entity = mapper.toEntity(request);
        UserEntity savedEntity = userService.createUser(entity);
        UserResponse response = assembler.toModel(savedEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User created successfully"));
    }

    /**
     * Get user by ID (authenticated users can view own profile; admin can view any)
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") UUID id) {
        UserEntity entity = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        UserResponse response = assembler.toModel(entity);

        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    /**
     * Get all users with pagination and optional search (admin only)
     * GET /api/v1/users?page=0&size=20&sort=createdAt,desc
     * GET /api/v1/users?search=keyword&page=0&size=20
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedModel<UserResponse>>> getAllUsers(
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserEntity> page;
        if (search != null && !search.isBlank()) {
            page = userService.searchUsers(search.trim(), pageable);
        } else {
            page = userService.getAllUsers(pageable);
        }
        PagedModel<UserResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Users retrieved successfully"));
    }

    /**
     * Get user statistics for admin dashboard
     * GET /api/v1/users/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats() {
        UserStatsResponse stats = userService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "User statistics retrieved successfully"));
    }

    /**
     * Update user by ID.
     * Users can update own profile; admin can update any user.
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequest request) {

        UUID callerId = UUID.fromString(jwt.getSubject());
        String callerRole = jwt.getClaimAsString("role");

        // Non-admin users can only update their own profile
        if (!"ADMIN".equals(callerRole) && !callerId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only update your own profile"));
        }

        UserEntity existingEntity = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        mapper.updateEntity(request, existingEntity);
        UserEntity updatedEntity = userService.updateUser(id, existingEntity);
        UserResponse response = assembler.toModel(updatedEntity);

        return ResponseEntity.ok(ApiResponse.success(response, "User updated successfully"));
    }

    /**
     * Delete user by ID (admin only)
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") UUID id) {
        if (!userService.userExists(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    /**
     * Get user by email (admin only)
     * GET /api/v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable("email") String email) {
        UserEntity entity = userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserResponse response = assembler.toModel(entity);

        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    /**
     * Get users by status with pagination (admin only)
     * GET /api/v1/users/status/{status}?page=0&size=20
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedModel<UserResponse>>> getUsersByStatus(
            @PathVariable("status") String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserEntity> page = userService.getUsersByStatus(status, pageable);
        PagedModel<UserResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Users retrieved successfully"));
    }

    // ──────────────────────────────────────────────
    //   Admin Actions: Ban/Unban & Role Change
    // ──────────────────────────────────────────────

    /**
     * Toggle user status (ACTIVE ↔ SUSPENDED) — admin only
     * PATCH /api/v1/users/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @RequestBody Map<String, String> body) {

        UUID adminId = UUID.fromString(jwt.getSubject());
        if (adminId.equals(id)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot change your own status"));
        }

        String newStatus = body.get("status");
        if (newStatus == null || (!newStatus.equals("ACTIVE") && !newStatus.equals("SUSPENDED"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Status must be ACTIVE or SUSPENDED"));
        }

        UserEntity updated = userService.changeUserStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(assembler.toModel(updated),
                "User status changed to " + newStatus));
    }

    /**
     * Change user role (USER ↔ ADMIN) — admin only
     * PATCH /api/v1/users/{id}/role
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @RequestBody Map<String, String> body) {

        UUID adminId = UUID.fromString(jwt.getSubject());
        if (adminId.equals(id)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot change your own role"));
        }

        String roleStr = body.get("role");
        Role newRole;
        try {
            newRole = Role.valueOf(roleStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Role must be USER or ADMIN"));
        }

        UserEntity updated = userService.changeUserRole(id, newRole);
        return ResponseEntity.ok(ApiResponse.success(assembler.toModel(updated),
                "User role changed to " + newRole));
    }
}
