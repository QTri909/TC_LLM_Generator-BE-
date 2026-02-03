package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for BusinessRule entity
 */
@Repository
public interface BusinessRuleRepository extends JpaRepository<BusinessRule, UUID> {

    /**
     * Find business rules by user story ID
     * @param userStoryId user story ID
     * @return List of business rules
     */
    List<BusinessRule> findByUserStory_UserStoryId(UUID userStoryId);

    /**
     * Find business rules by user story ID ordered by created date
     * @param userStoryId user story ID
     * @return List of business rules ordered by creation date
     */
    List<BusinessRule> findByUserStory_UserStoryIdOrderByCreatedAtAsc(UUID userStoryId);
}
