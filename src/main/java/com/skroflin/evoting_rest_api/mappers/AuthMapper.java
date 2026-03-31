package com.skroflin.evoting_rest_api.mappers;

import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "voterUUID", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "role", ignore = true)
    EligibleVoter toEntity(RegisterRequest registerRequest);
}
