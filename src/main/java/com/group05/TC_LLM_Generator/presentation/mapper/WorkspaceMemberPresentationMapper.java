package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateWorkspaceMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceMemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkspaceMemberPresentationMapper {

    @Mapping(target = "workspaceId", source = "workspace.workspaceId")
    @Mapping(target = "workspaceName", source = "workspace.name")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userFullName", source = "user.fullName")
    @Mapping(target = "userEmail", source = "user.email")
    WorkspaceMemberResponse toResponse(WorkspaceMember entity);

    List<WorkspaceMemberResponse> toResponseList(List<WorkspaceMember> entities);

    @Mapping(target = "workspaceMemberId", ignore = true)
    @Mapping(target = "workspace", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    void updateEntity(UpdateWorkspaceMemberRequest request, @MappingTarget WorkspaceMember entity);
}
