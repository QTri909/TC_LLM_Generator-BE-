package com.group05.TC_LLM_Generator.application.port.in.project;

import com.group05.TC_LLM_Generator.domain.model.entity.BusinessRule;
import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;

import java.util.List;
import java.util.UUID;

public interface UpdateProjectDetailsUseCase {
    ProjectDo execute(UUID projectId, String description, List<BusinessRule> businessRules);
}
