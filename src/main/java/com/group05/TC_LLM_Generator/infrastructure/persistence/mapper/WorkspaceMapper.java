package com.group05.TC_LLM_Generator.infrastructure.persistence.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProjectMapper.class })
public interface WorkspaceMapper {

    @Mapping(target = "id", source = "workspaceId")
    @Mapping(target = "ownerId", source = "ownerUser.userId")
    com.group05.TC_LLM_Generator.domain.model.entity.Workspace toDomain(Workspace entity);

    @Mapping(target = "workspaceId", source = "domain.id")
    @Mapping(target = "ownerUser", source = "ownerUser")
    @Mapping(target = "createdAt", source = "domain.createdAt")
    @Mapping(target = "updatedAt", source = "domain.updatedAt")
    Workspace toEntity(com.group05.TC_LLM_Generator.domain.model.entity.Workspace domain, UserEntity ownerUser);
}
