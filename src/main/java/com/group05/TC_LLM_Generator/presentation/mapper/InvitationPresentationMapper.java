package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import com.group05.TC_LLM_Generator.presentation.dto.response.InvitationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvitationPresentationMapper {

    @Mapping(target = "workspaceId", source = "workspace.workspaceId")
    @Mapping(target = "workspaceName", source = "workspace.name")
    @Mapping(target = "inviterUserId", source = "inviterUser.userId")
    @Mapping(target = "inviterName", source = "inviterUser.fullName")
    InvitationResponse toResponse(WorkspaceInvitation entity);

    List<InvitationResponse> toResponseList(List<WorkspaceInvitation> entities);
}
