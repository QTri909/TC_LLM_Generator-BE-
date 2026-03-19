package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.BusinessRuleRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.BusinessRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BusinessRuleRepositoryAdapter implements BusinessRuleRepositoryPort {

    private final BusinessRuleRepository jpaRepository;

    @Override
    public BusinessRule save(BusinessRule businessRule) {
        return jpaRepository.save(businessRule);
    }

    @Override
    public Optional<BusinessRule> findById(UUID businessRuleId) {
        return jpaRepository.findByIdWithStory(businessRuleId);
    }

    @Override
    public Page<BusinessRule> findByProjectId(UUID projectId, Pageable pageable) {
        return jpaRepository.findByProjectIdWithStory(projectId, pageable);
    }

    @Override
    public List<BusinessRule> findByUserStoryId(UUID userStoryId) {
        return jpaRepository.findByUserStoryIdWithStory(userStoryId);
    }

    @Override
    public void deleteById(UUID businessRuleId) {
        jpaRepository.deleteById(businessRuleId);
    }

    @Override
    public boolean existsById(UUID businessRuleId) {
        return jpaRepository.existsById(businessRuleId);
    }

    @Override
    public long countByProjectId(UUID projectId) {
        return jpaRepository.countByProject_ProjectId(projectId);
    }
}
