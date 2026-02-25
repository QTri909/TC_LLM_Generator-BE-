package com.group05.TC_LLM_Generator.infrastructure.persistence.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProjectMemberMapper.class, BusinessRuleMapper.class,
        UserStoryMapper.class })
public interface ProjectMapper {

    @Mapping(target = "id", source = "projectId")
    ProjectDo toDomain(Project entity);

    @Mapping(target = "projectId", source = "id")
    @Mapping(target = "workspace", ignore = true)
    @Mapping(target = "testCaseTemplates", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "jiraSiteId", ignore = true)
    @Mapping(target = "jiraProjectKey", ignore = true)
    Project toEntity(ProjectDo domain);
}
