package com.skroflin.evoting_rest_api.mappers;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.models.Election;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class}, imports = {com.skroflin.evoting_rest_api.enums.ElectionStatus.class})
public interface ElectionMapper {

    @Mapping(target = "electionName", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "electionStartTime", source = "startDate")
    @Mapping(target = "electionEndTime", source = "endDate")
    Election toEntity(ElectionRequest electionRequest);

    @Mapping(target = "id", source = "electionUUID")
    @Mapping(target = "title", source = "electionName")
    @Mapping(target = "startTime", source = "electionStartTime")
    @Mapping(target = "endTime", source = "electionEndTime")
    @Mapping(target = "isActive", expression = "java(election.getElectionStatus() == ElectionStatus.PREPARATION)")
    ElectionResponse toResponse(Election election);

    List<ElectionResponse> toResponseList(List<Election> elections);

    @AfterMapping
    default void linkCandidates(@MappingTarget Election election) {
        if (election.getCandidates() != null) {
            election.getCandidates().forEach(candidate -> candidate.setElection(election));
        }
    }
}
