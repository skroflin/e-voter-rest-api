package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.request.CandidateRequest;
import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.exceptions.election.CandidateAlreadyExists;
import com.skroflin.evoting_rest_api.exceptions.election.InvalidElectionException;
import com.skroflin.evoting_rest_api.mappers.ElectionMapper;
import com.skroflin.evoting_rest_api.models.Candidate;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import com.skroflin.evoting_rest_api.service.ElectionService;
import com.skroflin.evoting_rest_api.service.validation.ElectionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ElectionServiceImplTest {

    @Mock
    private ElectionRepository electionRepository;
    @Mock
    private ElectionMapper electionMapper;
    @Mock
    private ElectionValidator electionValidator;

    @InjectMocks
    private ElectionServiceImpl electionService;

    private Election election;
    private ElectionRequest electionRequest;

    @Test
    void setUp() {
        electionRequest = new ElectionRequest(
                "Izbori 2026 - Test", "Opis - Test",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), List.of()
        );

        election = new Election();
        election.setElectionName(electionRequest.title());
    }

    @Test
    void createElection_Success() {
        Election mockedElection = new Election();

        when(electionMapper.toEntity(electionRequest)).thenReturn(mockedElection);
        when(electionRepository.save(any(Election.class))).thenReturn(mockedElection);
        when(electionMapper.toResponse(any())).thenReturn(
                new ElectionResponse(
                        UUID.randomUUID(), "Izbori 2026 - Test",
                        "Opis - Test", null, null,
                        List.of(), true
                )
        );

        ElectionResponse electionResponse = electionService.createElection(electionRequest);
        assertNotNull(electionResponse);
        assertEquals("Izbori 2026 - Test", electionResponse.title());
        verify(electionValidator).validateElectionRequest(electionRequest);
        verify(electionRepository).save(mockedElection);
    }

    @Test
    void createElection_ValidationError() {
        doThrow(new InvalidElectionException("Bad election dates"))
                .when(electionValidator).validateElectionRequest(electionRequest);

        assertThrows(InvalidElectionException.class, () -> electionService.createElection(electionRequest));
        verify(electionRepository, never()).save(election);
    }

    @Test
    void getAllElections_Success() {
        Pageable mockPageable = PageRequest.of(0, 10);
        List<Election> mockElections = List.of(new Election(), new Election());
        Page<Election> mockElectionPage = new PageImpl<>(mockElections, mockPageable, mockElections.size());

        when(electionRepository.findAll(mockPageable)).thenReturn(mockElectionPage);
        when(electionMapper.toResponse(any(Election.class))).thenReturn(mock(ElectionResponse.class));

        Page<ElectionResponse> result = electionService.getAllElections(mockPageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(electionRepository).findAll(mockPageable);
    }

    @Test
    void getElectionById_success() {
        UUID mockId = UUID.randomUUID();
        Election mockElection = new Election();
        mockElection.setElectionUUID(mockId);

        when(electionRepository.findById(mockId)).thenReturn(Optional.of(mockElection));
        when(electionMapper.toResponse(mockElection)).thenReturn(new ElectionResponse(
                mockId, "Test Election", "Test description", null, null, List.of(), true
        ));

        ElectionResponse mockResult = electionService.getElectionById(mockId);

        assertEquals(mockId, mockResult.id());
    }

    @Test
    void addCandidate_shouldThrowException_whenDuplicateName() {
        UUID mockId = UUID.randomUUID();
        Election mockElection = new Election();
        mockElection.setElectionStatus(ElectionStatus.PREPARATION);

        Candidate mockCandidate = new Candidate();
        mockCandidate.setCandidateFullName("John Doe");
        mockElection.getCandidates().add(mockCandidate);

        CandidateRequest mockCandidateRequest = new CandidateRequest(
                "John Doe", "Test bio"
        );

        when(electionRepository.findById(mockId)).thenReturn(Optional.of(mockElection));

        assertThrows(CandidateAlreadyExists.class, () -> electionService.addCandidateToElection(mockId, mockCandidateRequest));
    }
}
