package com.skroflin.evoting_rest_api.mappers;

import com.skroflin.evoting_rest_api.dto.RegisterRequest;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "voterUUID", ignore = true)
    @Mapping(target = "isEnabled", ignore = true)
    @Mapping(target = "role", ignore = true)
    EligibleVoter toEntity(RegisterRequest registerRequest);
}
