package com.group05.TC_LLM_Generator.presentation.dto.request;

import com.group05.TC_LLM_Generator.presentation.dto.BusinessRuleDTO;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateProjectDetailsRequest {
    private UUID projectId;
    private String name;
    private String projectKey;
    private String description;
    private List<BusinessRuleDTO> businessRules;
}
