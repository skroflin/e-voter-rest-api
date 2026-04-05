package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.exceptions.election.InvalidElectionException;
import com.skroflin.evoting_rest_api.mappers.ElectionMapper;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import com.skroflin.evoting_rest_api.service.validation.ElectionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
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
}
