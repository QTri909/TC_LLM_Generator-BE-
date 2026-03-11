package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.UserRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for User entity
 * Handles CRUD operations and user-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepositoryPort userRepository;

    /**
     * Create a new user
     */
    @Transactional
    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    /**
     * Get user by ID
     */
    public Optional<UserEntity> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get user by email
     */
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users
     */
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all users with pagination
     */
    public Page<UserEntity> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Get users by status
     */
    public List<UserEntity> getUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }

    /**
     * Get users by status with pagination
     */
    public Page<UserEntity> getUsersByStatus(String status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    /**
     * Search users by name or email with pagination
     */
    public Page<UserEntity> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchByNameOrEmail(keyword, pageable);
    }

    /**
     * Get user statistics for admin dashboard
     */
    public UserStatsResponse getUserStats() {
        long total = userRepository.count();
        long active = userRepository.countByStatus("ACTIVE");
        long suspended = userRepository.countByStatus("SUSPENDED");

        return UserStatsResponse.builder()
                .totalUsers(total)
                .activeUsers(active)
                .suspendedUsers(suspended)
                .build();
    }

    /**
     * Update user
     */
    @Transactional
    public UserEntity updateUser(UUID userId, UserEntity updatedUser) {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Update fields
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        if (updatedUser.getFullName() != null) {
            existingUser.setFullName(updatedUser.getFullName());
        }
        
        if (updatedUser.getStatus() != null) {
            existingUser.setStatus(updatedUser.getStatus());
        }
        
        if (updatedUser.getPasswordHash() != null) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Delete user by ID
     */
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Check if user exists
     */
    public boolean userExists(UUID userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
