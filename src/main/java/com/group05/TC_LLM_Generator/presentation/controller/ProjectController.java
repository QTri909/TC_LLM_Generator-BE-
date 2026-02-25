package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.port.in.project.CreateProjectUseCase;
import com.group05.TC_LLM_Generator.application.port.in.project.UpdateProjectDetailsUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.BusinessRule;
import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateProjectRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateProjectDetailsRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.ProjectResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.BusinessRulePresentationMapper;
import com.group05.TC_LLM_Generator.presentation.mapper.ProjectPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

        private final CreateProjectUseCase createProjectUseCase;
        private final UpdateProjectDetailsUseCase updateProjectDetailsUseCase;
        private final ProjectPresentationMapper projectMapper;
        private final BusinessRulePresentationMapper businessRuleMapper;

        @PostMapping
        public ResponseEntity<ProjectResponse> createProject(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody CreateProjectRequest request) {

                UUID userId = UUID.fromString(jwt.getSubject());
                ProjectDo project = createProjectUseCase.execute(request.getName(), userId);

                return ResponseEntity.ok(projectMapper.toResponse(project));
        }

        @PostMapping("/details")
        public ResponseEntity<ProjectResponse> updateProjectDetails(
                        @RequestBody UpdateProjectDetailsRequest request) {

                List<BusinessRule> businessRules = null;
                if (request.getBusinessRules() != null) {
                        businessRules = request.getBusinessRules().stream()
                                        .map(businessRuleMapper::toDomain)
                                        .collect(Collectors.toList());
                }

                ProjectDo project = updateProjectDetailsUseCase.execute(
                                request.getProjectId(),
                                request.getDescription(),
                                businessRules);

                return ResponseEntity.ok(projectMapper.toResponse(project));
        }
}
