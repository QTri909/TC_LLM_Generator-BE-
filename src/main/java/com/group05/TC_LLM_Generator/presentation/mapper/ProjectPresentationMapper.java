package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { BusinessRulePresentationMapper.class })
public interface ProjectPresentationMapper {

    @Mapping(target = "projectId", source = "id")
    @Mapping(target = "displayName", expression = "java(project.getProjectKey() + \" - \" + project.getName())")
    ProjectResponse toResponse(ProjectDo project);
}
