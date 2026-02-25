package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.BusinessRule;
import com.group05.TC_LLM_Generator.presentation.dto.BusinessRuleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessRulePresentationMapper {
    BusinessRuleDTO toDTO(BusinessRule domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BusinessRule toDomain(BusinessRuleDTO dto);
}
