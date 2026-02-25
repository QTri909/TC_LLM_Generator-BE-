package com.group05.TC_LLM_Generator.infrastructure.persistence.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.BusinessRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessRuleMapper {
    @Mapping(target = "id", source = "businessRuleId")
    BusinessRule toDomain(com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule entity);

    @Mapping(target = "businessRuleId", source = "id")
    @Mapping(target = "project", ignore = true)
    com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule toEntity(BusinessRule domain);
}
