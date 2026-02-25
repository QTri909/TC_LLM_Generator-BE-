package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.port.in.workspace.GetWorkspaceUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final GetWorkspaceUseCase getWorkspaceUseCase;

    @GetMapping("/me")
    public ResponseEntity<Workspace> getMyWorkspace(@AuthenticationPrincipal Jwt jwt) {
        String userIdString = jwt.getSubject();
        UUID userId = UUID.fromString(userIdString);

        return getWorkspaceUseCase.execute(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
