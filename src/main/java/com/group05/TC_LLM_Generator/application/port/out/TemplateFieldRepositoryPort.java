package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TemplateField;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateFieldRepositoryPort {

    TemplateField save(TemplateField field);

    List<TemplateField> saveAll(List<TemplateField> fields);

    Optional<TemplateField> findById(UUID fieldId);

    List<TemplateField> findByTemplateIdOrdered(UUID templateId);

    void deleteById(UUID fieldId);

    void deleteByTemplateId(UUID templateId);
}
