package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRuleResponse extends RepresentationModel<BusinessRuleResponse> {

    private UUID businessRuleId;
    private UUID projectId;
    private UUID userStoryId;
    private String userStoryTitle;
    private String title;
    private String description;
    private Integer priority;
    private String source;
    private Instant createdAt;
}
