package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Find user by email
     * @param email user's email
     * @return Optional of UserEntity
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if email exists
     * @param email user's email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by status
     * @param status user status
     * @return List of users with the specified status
     */
    java.util.List<UserEntity> findByStatus(String status);
}
