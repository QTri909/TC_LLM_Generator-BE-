package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessRuleRepositoryPort {

    BusinessRule save(BusinessRule businessRule);

    Optional<BusinessRule> findById(UUID businessRuleId);

    Page<BusinessRule> findByProjectId(UUID projectId, Pageable pageable);

    List<BusinessRule> findByUserStoryId(UUID userStoryId);

    void deleteById(UUID businessRuleId);

    boolean existsById(UUID businessRuleId);

    long countByProjectId(UUID projectId);
}
