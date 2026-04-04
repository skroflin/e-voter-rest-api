package com.skroflin.evoting_rest_api.mappers;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.models.Election;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ElectionMapper {

    @Mapping(target = "electionName", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "electionStartDate", source = "startDate")
    @Mapping(target = "electionEndDate", source = "endDate")
    Election toEntity(ElectionRequest electionRequest);

    @Mapping(target = "id", source = "electionUUID")
    @Mapping(target = "title", source = "electionName")
    @Mapping(target = "startDate", source = "electionStartDate")
    @Mapping(target = "endDate", source = "electionEndDate")
    ElectionResponse toResponse(Election election);

    List<ElectionResponse> toResponseList(List<Election> elections);

    @AfterMapping
    default void linkCandidates(@MappingTarget Election election) {
        if (election.getCandidates() != null) {
            election.getCandidates().forEach(candidate -> candidate.setElection(election));
        }
    }
}
