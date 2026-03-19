package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BusinessRule entity
 */
@Repository
public interface BusinessRuleRepository extends JpaRepository<BusinessRule, UUID> {

    @Query("SELECT br FROM BusinessRule br LEFT JOIN FETCH br.userStory LEFT JOIN FETCH br.project " +
           "WHERE br.userStory.userStoryId = :userStoryId ORDER BY br.createdAt ASC")
    List<BusinessRule> findByUserStoryIdWithStory(@Param("userStoryId") UUID userStoryId);

    @Query(value = "SELECT br FROM BusinessRule br LEFT JOIN FETCH br.userStory LEFT JOIN FETCH br.project " +
                   "WHERE br.project.projectId = :projectId",
           countQuery = "SELECT COUNT(br) FROM BusinessRule br WHERE br.project.projectId = :projectId")
    Page<BusinessRule> findByProjectIdWithStory(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("SELECT br FROM BusinessRule br LEFT JOIN FETCH br.userStory LEFT JOIN FETCH br.project " +
           "WHERE br.businessRuleId = :id")
    Optional<BusinessRule> findByIdWithStory(@Param("id") UUID id);

    long countByProject_ProjectId(UUID projectId);
}
