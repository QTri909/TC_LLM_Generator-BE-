package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.UserRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements UserRepositoryPort using Spring Data JPA
 * Bridges the application layer with the infrastructure layer
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository jpaRepository;

    @Override
    public UserEntity save(UserEntity user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID userId) {
        return jpaRepository.findById(userId);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public List<UserEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<UserEntity> findByStatus(String status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public void deleteById(UUID userId) {
        jpaRepository.deleteById(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return jpaRepository.existsById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
