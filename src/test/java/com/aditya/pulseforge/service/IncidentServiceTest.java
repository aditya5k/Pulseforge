
package com.aditya.pulseforge.service;

import com.aditya.pulseforge.dto.IncidentRequest;
import com.aditya.pulseforge.dto.IncidentResponse;
import com.aditya.pulseforge.entity.Incident;
import com.aditya.pulseforge.entity.MonitoredService;
import com.aditya.pulseforge.enums.IncidentSeverity;
import com.aditya.pulseforge.enums.IncidentStatus;
import com.aditya.pulseforge.exception.ResourceNotFoundException;
import com.aditya.pulseforge.mapper.IncidentMapper;
import com.aditya.pulseforge.repository.IncidentRepository;
import com.aditya.pulseforge.repository.MonitoredServiceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private MonitoredServiceRepository monitoredServiceRepository;

    @Mock
    private IncidentMapper incidentMapper;

    @InjectMocks
    private IncidentService incidentService;

    private MonitoredService service;
    private Incident incident;
    private IncidentRequest request;
    private IncidentResponse response;

    @BeforeEach
    void setUp() {
        service = new MonitoredService();
        service.setId(1);
        service.setName("Payment Service");

        incident = new Incident();
        incident.setId(10);
        incident.setTitle("Payment API failure");
        incident.setSeverity(IncidentSeverity.HIGH);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setEnvironment("PRODUCTION");
        incident.setService(service);

        request = new IncidentRequest();
        request.setTitle("Payment API failure");
        request.setSeverity(IncidentSeverity.HIGH);
        request.setStatus(IncidentStatus.OPEN);
        request.setEnvironment("PRODUCTION");
        request.setStartedAt(LocalDateTime.now());
        request.setServiceId(1);

        response = new IncidentResponse();
        response.setId(10);
        response.setTitle("Payment API failure");
        response.setServiceId(1);
        response.setServiceName("Payment Service");
    }

    // CREATE INCIDENT

    @Test
    void createIncident_shouldReturnSavedIncident() {
        when(monitoredServiceRepository.findById(1))
                .thenReturn(Optional.of(service));
        when(incidentMapper.toEntity(request, service))
                .thenReturn(incident);
        when(incidentRepository.save(incident))
                .thenReturn(incident);
        when(incidentMapper.toResponse(incident))
                .thenReturn(response);

        IncidentResponse result =
                incidentService.createIncident(request);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Payment API failure", result.getTitle());

        verify(incidentRepository).save(incident);
    }

    @Test
    void createIncident_shouldThrowWhenServiceDoesNotExist() {
        when(monitoredServiceRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> incidentService.createIncident(request)
        );

        verify(incidentRepository, never()).save(any());
    }

    // RETRIEVE INCIDENT

    @Test
    void getIncidentById_shouldReturnIncident() {
        when(incidentRepository.findById(10))
                .thenReturn(Optional.of(incident));
        when(incidentMapper.toResponse(incident))
                .thenReturn(response);

        IncidentResponse result =
                incidentService.getIncidentById(10);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Payment API failure", result.getTitle());
    }

    @Test
    void getIncidentById_shouldThrowWhenIncidentDoesNotExist() {
        when(incidentRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> incidentService.getIncidentById(999)
        );
    }

    // UPDATE INCIDENT

    @Test
    void updateIncident_shouldUpdateExistingIncident() {
        when(incidentRepository.findById(10))
                .thenReturn(Optional.of(incident));
        when(monitoredServiceRepository.findById(1))
                .thenReturn(Optional.of(service));

        doNothing().when(incidentMapper)
                .updateEntity(incident, request, service);

        when(incidentRepository.save(incident))
                .thenReturn(incident);
        when(incidentMapper.toResponse(incident))
                .thenReturn(response);

        IncidentResponse result =
                incidentService.updateIncident(10, request);

        assertNotNull(result);
        assertEquals(10, result.getId());

        verify(incidentMapper)
                .updateEntity(incident, request, service);
        verify(incidentRepository).save(incident);
    }

    @Test
    void updateIncident_shouldThrowWhenIncidentDoesNotExist() {
        when(incidentRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> incidentService.updateIncident(999, request)
        );

        verify(incidentRepository, never()).save(any());
    }

    @Test
    void updateIncident_shouldThrowWhenServiceDoesNotExist() {
        when(incidentRepository.findById(10))
                .thenReturn(Optional.of(incident));
        when(monitoredServiceRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> incidentService.updateIncident(10, request)
        );

        verify(incidentRepository, never()).save(any());
    }

    // DELETE INCIDENT

    @Test
    void deleteIncident_shouldDeleteExistingIncident() {
        when(incidentRepository.findById(10))
                .thenReturn(Optional.of(incident));

        assertDoesNotThrow(
                () -> incidentService.deleteIncident(10)
        );

        verify(incidentRepository).delete((Incident) incident);
    }

    @Test
    void deleteIncident_shouldThrowWhenIncidentDoesNotExist() {
        when(incidentRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> incidentService.deleteIncident(999)
        );

        verify(incidentRepository, never()).delete(any(Incident.class));
    }
}
