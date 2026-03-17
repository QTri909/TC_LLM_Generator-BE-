package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceMemberService;
import com.group05.TC_LLM_Generator.application.service.WorkspaceService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import com.group05.TC_LLM_Generator.presentation.assembler.WorkspaceMemberModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.AddWorkspaceMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateWorkspaceMemberRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.WorkspaceMemberResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspace-members")
@RequiredArgsConstructor
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final WorkspaceMemberModelAssembler assembler;
    private final PagedResourcesAssembler<WorkspaceMember> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> addWorkspaceMember(
            @Valid @RequestBody AddWorkspaceMemberRequest request) {

        Workspace workspace = workspaceService.getWorkspaceById(request.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", request.getWorkspaceId()));

        UserEntity user = userService.getUserById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        WorkspaceMember savedMember = workspaceMemberService.addMember(workspace, user, request.getRole());
        WorkspaceMemberResponse response = assembler.toModel(savedMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Workspace member added successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> getWorkspaceMemberById(
            @PathVariable("id") UUID id) {

        WorkspaceMember member = workspaceMemberService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkspaceMember", "id", id));

        WorkspaceMemberResponse response = assembler.toModel(member);

        return ResponseEntity.ok(ApiResponse.success(response, "Workspace member retrieved successfully"));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<PagedModel<WorkspaceMemberResponse>>> getWorkspaceMembersByWorkspace(
            @PathVariable("workspaceId") UUID workspaceId,
            @PageableDefault(size = 20, sort = "joinedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<WorkspaceMember> page = workspaceMemberService.getByWorkspaceId(workspaceId, pageable);
        PagedModel<WorkspaceMemberResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Workspace members retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> updateWorkspaceMemberRole(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateWorkspaceMemberRequest request) {

        WorkspaceMember updated = workspaceMemberService.updateRole(id, request.getRole());
        WorkspaceMemberResponse response = assembler.toModel(updated);

        return ResponseEntity.ok(ApiResponse.success(response, "Workspace member updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeWorkspaceMember(@PathVariable("id") UUID id) {
        if (!workspaceMemberService.getById(id).isPresent()) {
            throw new ResourceNotFoundException("WorkspaceMember", "id", id);
        }

        workspaceMemberService.removeMember(id);

        return ResponseEntity.ok(ApiResponse.success("Workspace member removed successfully"));
    }
}
