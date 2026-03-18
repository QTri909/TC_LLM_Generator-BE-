package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.TemplateFieldRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TemplateField;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TemplateFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TemplateFieldRepositoryAdapter implements TemplateFieldRepositoryPort {

    private final TemplateFieldRepository jpaRepository;

    @Override
    public TemplateField save(TemplateField field) {
        return jpaRepository.save(field);
    }

    @Override
    public List<TemplateField> saveAll(List<TemplateField> fields) {
        return jpaRepository.saveAll(fields);
    }

    @Override
    public Optional<TemplateField> findById(UUID fieldId) {
        return jpaRepository.findById(fieldId);
    }

    @Override
    public List<TemplateField> findByTemplateIdOrdered(UUID templateId) {
        return jpaRepository.findByTestCaseTemplate_TestCaseTemplateIdOrderByDisplayOrderAsc(templateId);
    }

    @Override
    public void deleteById(UUID fieldId) {
        jpaRepository.deleteById(fieldId);
    }

    @Override
    public void deleteByTemplateId(UUID templateId) {
        List<TemplateField> fields = jpaRepository.findByTestCaseTemplate_TestCaseTemplateId(templateId);
        jpaRepository.deleteAll(fields);
    }
}
