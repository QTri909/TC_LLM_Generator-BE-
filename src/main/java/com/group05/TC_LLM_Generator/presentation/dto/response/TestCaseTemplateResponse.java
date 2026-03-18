package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseTemplateResponse {

    private UUID testCaseTemplateId;
    private UUID projectId;
    private String name;
    private String description;
    private Boolean isDefault;
    private List<TemplateFieldResponse> fields;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateFieldResponse {
        private UUID templateFieldId;
        private String fieldKey;
        private String fieldLabel;
        private String fieldType;
        private Boolean isRequired;
        private Integer displayOrder;
    }
}
