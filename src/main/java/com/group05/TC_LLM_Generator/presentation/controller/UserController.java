package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.UserService;
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
import org.springframework.web.bind.annotation.*;

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
     * Create a new user
     * POST /api/v1/users
     */
    @PostMapping
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
     * Get user by ID
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
     * Get all users with pagination and optional search
     * GET /api/v1/users?page=0&size=20&sort=createdAt,desc
     * GET /api/v1/users?search=keyword&page=0&size=20
     */
    @GetMapping
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
     * Update user by ID
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserEntity existingEntity = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        mapper.updateEntity(request, existingEntity);
        UserEntity updatedEntity = userService.updateUser(id, existingEntity);
        UserResponse response = assembler.toModel(updatedEntity);

        return ResponseEntity.ok(ApiResponse.success(response, "User updated successfully"));
    }

    /**
     * Delete user by ID
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") UUID id) {
        if (!userService.userExists(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    /**
     * Get user by email
     * GET /api/v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable("email") String email) {
        UserEntity entity = userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserResponse response = assembler.toModel(entity);

        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    /**
     * Get users by status with pagination
     * GET /api/v1/users/status/{status}?page=0&size=20
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PagedModel<UserResponse>>> getUsersByStatus(
            @PathVariable("status") String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserEntity> page = userService.getUsersByStatus(status, pageable);
        PagedModel<UserResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Users retrieved successfully"));
    }
}
