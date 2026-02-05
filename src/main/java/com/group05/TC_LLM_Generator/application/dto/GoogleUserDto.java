package com.group05.TC_LLM_Generator.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserDto {
    private String email;
    private String name;
    private String pictureUrl;
}
