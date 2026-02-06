package com.group05.TC_LLM_Generator.application.port.in;

import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;

import java.util.List;
import java.util.UUID;

public interface GetWorkspaceUseCase {
    List<Workspace> getWorkspaceForUser(UUID userId);
}
