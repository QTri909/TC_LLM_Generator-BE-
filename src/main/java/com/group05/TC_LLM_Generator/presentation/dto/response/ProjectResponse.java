package com.group05.TC_LLM_Generator.presentation.dto.response;

import com.group05.TC_LLM_Generator.presentation.dto.BusinessRuleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private UUID projectId;
    private String projectKey;
    private String name;
    private String displayName; // projectKey + " - " + name
    private String description;
    private List<BusinessRuleDTO> businessRules;
}
