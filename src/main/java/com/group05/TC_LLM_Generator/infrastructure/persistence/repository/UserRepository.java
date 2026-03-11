package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.domain.model.enums.Role;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findByStatus(String status);

    Page<UserEntity> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);

    Page<UserEntity> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    // Admin overview queries
    long countByRole(Role role);

    List<UserEntity> findTop5ByOrderByCreatedAtDesc();

    long countByCreatedAtBetween(Instant start, Instant end);

    @Query("SELECT CAST(u.createdAt AS date) as day, COUNT(u) as cnt " +
           "FROM UserEntity u WHERE u.createdAt >= :since " +
           "GROUP BY CAST(u.createdAt AS date) ORDER BY day ASC")
    List<Object[]> countUsersGroupedByDay(@Param("since") Instant since);
}
