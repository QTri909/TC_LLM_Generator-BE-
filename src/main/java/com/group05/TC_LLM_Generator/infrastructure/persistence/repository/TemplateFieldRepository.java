package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TemplateField entity
 */
@Repository
public interface TemplateFieldRepository extends JpaRepository<TemplateField, UUID> {

    /**
     * Find template fields by template ID
     * @param templateId test case template ID
     * @return List of template fields
     */
    List<TemplateField> findByTestCaseTemplate_TestCaseTemplateId(UUID templateId);

    /**
     * Find template fields by template ID ordered by display order
     * @param templateId test case template ID
     * @return List of template fields ordered by display order
     */
    List<TemplateField> findByTestCaseTemplate_TestCaseTemplateIdOrderByDisplayOrderAsc(UUID templateId);

    /**
     * Find required template fields
     * @param templateId test case template ID
     * @return List of required template fields
     */
    List<TemplateField> findByTestCaseTemplate_TestCaseTemplateIdAndIsRequiredTrue(UUID templateId);
}
