package com.group05.TC_LLM_Generator.application.port.in.project;

import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import java.util.UUID;

public interface CreateProjectUseCase {
    ProjectDo execute(String name, UUID userId);
}
