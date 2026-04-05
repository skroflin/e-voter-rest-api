package com.skroflin.evoting_rest_api.mappers;

import com.skroflin.evoting_rest_api.dto.request.CandidateRequest;
import com.skroflin.evoting_rest_api.models.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = "candidateFullName", source = "name")
    @Mapping(target = "description", source = "bio")
    Candidate toEntity(CandidateRequest candidateRequest);
}
