package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for BusinessRule entity
 */
@Repository
public interface BusinessRuleRepository extends JpaRepository<BusinessRule, UUID> {

    List<BusinessRule> findByUserStory_UserStoryId(UUID userStoryId);

    List<BusinessRule> findByUserStory_UserStoryIdOrderByCreatedAtAsc(UUID userStoryId);

    Page<BusinessRule> findByProject_ProjectId(UUID projectId, Pageable pageable);

    long countByProject_ProjectId(UUID projectId);
}
