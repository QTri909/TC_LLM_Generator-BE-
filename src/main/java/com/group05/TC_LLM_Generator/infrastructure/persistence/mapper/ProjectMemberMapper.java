package com.group05.TC_LLM_Generator.infrastructure.persistence.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "displayName", source = "user.fullName")
    ProjectMember toDomain(com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember entity);

    @Mapping(target = "projectMemberId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user", ignore = true)
    com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember toEntity(ProjectMember domain);
}
