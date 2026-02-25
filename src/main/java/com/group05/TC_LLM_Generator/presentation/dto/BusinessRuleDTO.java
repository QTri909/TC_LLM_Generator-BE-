package com.group05.TC_LLM_Generator.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRuleDTO {
    private String title;
    private String description;
    private Integer priority;
}
