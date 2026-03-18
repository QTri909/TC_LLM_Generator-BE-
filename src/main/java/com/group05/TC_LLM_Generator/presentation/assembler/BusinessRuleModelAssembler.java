package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import com.group05.TC_LLM_Generator.presentation.controller.BusinessRuleController;
import com.group05.TC_LLM_Generator.presentation.dto.response.BusinessRuleResponse;
import org.hibernate.Hibernate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BusinessRuleModelAssembler implements RepresentationModelAssembler<BusinessRule, BusinessRuleResponse> {

    @Override
    @NonNull
    public BusinessRuleResponse toModel(@NonNull BusinessRule entity) {
        BusinessRuleResponse response = BusinessRuleResponse.builder()
                .businessRuleId(entity.getBusinessRuleId())
                .projectId(entity.getProject().getProjectId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .source(entity.getSource())
                .createdAt(entity.getCreatedAt())
                .build();

        // Denormalize user story title
        if (entity.getUserStory() != null && Hibernate.isInitialized(entity.getUserStory())) {
            response.setUserStoryId(entity.getUserStory().getUserStoryId());
            response.setUserStoryTitle(entity.getUserStory().getTitle());
        }

        // HATEOAS self link
        response.add(linkTo(methodOn(BusinessRuleController.class)
                .getBusinessRule(entity.getProject().getProjectId(), entity.getBusinessRuleId()))
                .withSelfRel());

        return response;
    }
}
