package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for User repository operations.
 * Defines the contract for persistence operations on User entities.
 */
public interface UserRepositoryPort {

    UserEntity save(UserEntity user);

    Optional<UserEntity> findById(UUID userId);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAll();

    Page<UserEntity> findAll(Pageable pageable);

    List<UserEntity> findByStatus(String status);

    Page<UserEntity> findByStatus(String status, Pageable pageable);

    void deleteById(UUID userId);

    boolean existsById(UUID userId);

    boolean existsByEmail(String email);

    long count();

    long countByStatus(String status);

    Page<UserEntity> searchByNameOrEmail(String keyword, Pageable pageable);
}
