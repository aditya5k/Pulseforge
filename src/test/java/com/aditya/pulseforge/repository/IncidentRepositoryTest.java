package com.aditya.pulseforge.repository;

import com.aditya.pulseforge.entity.Incident;
import com.aditya.pulseforge.entity.MonitoredService;
import com.aditya.pulseforge.enums.IncidentSeverity;
import com.aditya.pulseforge.enums.IncidentStatus;
import com.aditya.pulseforge.specification.IncidentSpecification;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IncidentRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private MonitoredServiceRepository monitoredServiceRepository;

    // Helper method to create a monitored service

    private MonitoredService createService(String name) {

        MonitoredService service = new MonitoredService();

        service.setName(name);
        service.setDescription("Test monitored service");
        service.setTeamName("Backend Team");
        service.setEnvironment("PRODUCTION");

        return monitoredServiceRepository.saveAndFlush(service);
    }

    // Helper method to create an incident

    private Incident createIncident(
            String title,
            IncidentSeverity severity,
            IncidentStatus status,
            String environment,
            LocalDateTime startedAt,
            MonitoredService service) {

        Incident incident = new Incident();

        incident.setTitle(title);
        incident.setDescription("Test incident description");
        incident.setSeverity(severity);
        incident.setStatus(status);
        incident.setEnvironment(environment);
        incident.setStartedAt(startedAt);
        incident.setService(service);

        return incidentRepository.saveAndFlush(incident);
    }

    // TEST 1: SAVE INCIDENT

    @Test
    void saveIncident_shouldPersistIncident() {

        MonitoredService service = createService("Payment Service");

        Incident incident = createIncident(
                "Payment API failure",
                IncidentSeverity.HIGH,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.of(2026, 11, 9, 10, 30),
                service
        );

        assertNotNull(incident.getId());

        Incident savedIncident = incidentRepository
                .findById(incident.getId())
                .orElseThrow();

        assertEquals("Payment API failure", savedIncident.getTitle());
        assertEquals(IncidentSeverity.HIGH, savedIncident.getSeverity());
        assertEquals(IncidentStatus.OPEN, savedIncident.getStatus());

        assertEquals(
                service.getId(),
                savedIncident.getService().getId()
        );
    }

    // TEST 2: FIND INCIDENT BY ID

    @Test
    void findById_shouldReturnIncident() {

        MonitoredService service = createService("Payment Service");

        Incident incident = createIncident(
                "Database timeout",
                IncidentSeverity.CRITICAL,
                IncidentStatus.INVESTIGATING,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        Incident result = incidentRepository
                .findById(incident.getId())
                .orElseThrow();

        assertEquals(incident.getId(), result.getId());
        assertEquals("Database timeout", result.getTitle());
        assertEquals(IncidentSeverity.CRITICAL, result.getSeverity());
    }

    // TEST 3: FIND ALL INCIDENTS

    @Test
    void findAll_shouldReturnAllIncidents() {

        MonitoredService service = createService("Payment Service");

        createIncident(
                "Payment failure",
                IncidentSeverity.HIGH,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        createIncident(
                "Database failure",
                IncidentSeverity.MEDIUM,
                IncidentStatus.INVESTIGATING,
                "STAGING",
                LocalDateTime.now(),
                service
        );

        List<Incident> incidents = incidentRepository.findAll();

        assertEquals(2, incidents.size());
    }

    // TEST 4: DELETE INCIDENT

    @Test
    void delete_shouldRemoveIncident() {

        MonitoredService service = createService("Payment Service");

        Incident incident = createIncident(
                "Payment failure",
                IncidentSeverity.HIGH,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        Integer incidentId = incident.getId();

        incidentRepository.delete(incident);
        incidentRepository.flush();

        assertFalse(incidentRepository.existsById(incidentId));
    }

    // TEST 5: FILTER BY SEVERITY AND STATUS

    @Test
    void findAll_shouldFilterBySeverityAndStatus() {

        MonitoredService service = createService("Payment Service");

        createIncident(
                "Critical payment failure",
                IncidentSeverity.CRITICAL,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        createIncident(
                "Medium payment failure",
                IncidentSeverity.MEDIUM,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        createIncident(
                "Resolved critical failure",
                IncidentSeverity.CRITICAL,
                IncidentStatus.RESOLVED,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        var specification =
                IncidentSpecification.hasSeverity(IncidentSeverity.CRITICAL)
                        .and(IncidentSpecification.hasStatus(IncidentStatus.OPEN));

        List<Incident> result =
                incidentRepository.findAll(specification);

        assertEquals(1, result.size());
        assertEquals(
                "Critical payment failure",
                result.get(0).getTitle()
        );
    }

    // TEST 6: FILTER BY ENVIRONMENT

    @Test
    void findAll_shouldFilterByEnvironment() {

        MonitoredService service = createService("Payment Service");

        createIncident(
                "Production failure",
                IncidentSeverity.HIGH,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                service
        );

        createIncident(
                "Staging failure",
                IncidentSeverity.MEDIUM,
                IncidentStatus.OPEN,
                "STAGING",
                LocalDateTime.now(),
                service
        );

        var specification =
                IncidentSpecification.hasEnvironment("production");

        List<Incident> result =
                incidentRepository.findAll(specification);

        assertEquals(1, result.size());
        assertEquals("Production failure", result.get(0).getTitle());
    }

    // TEST 7: FILTER BY MONITORED SERVICE ID

    @Test
    void findAll_shouldFilterByServiceId() {

        MonitoredService paymentService =
                createService("Payment Service");

        MonitoredService orderService =
                createService("Order Service");

        createIncident(
                "Payment failure",
                IncidentSeverity.HIGH,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                paymentService
        );

        createIncident(
                "Order failure",
                IncidentSeverity.MEDIUM,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.now(),
                orderService
        );

        var specification =
                IncidentSpecification.hasServiceId(paymentService.getId());

        List<Incident> result =
                incidentRepository.findAll(specification);

        assertEquals(1, result.size());

        assertEquals(
                paymentService.getId(),
                result.get(0).getService().getId()
        );
    }

    // TEST 8: PAGINATION AND SORTING

    @Test
    void findAll_shouldSupportPaginationAndSorting() {

        MonitoredService service = createService("Payment Service");

        createIncident(
                "Low severity incident",
                IncidentSeverity.LOW,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.of(2026, 11, 7, 10, 0),
                service
        );

        createIncident(
                "High severity incident",
                IncidentSeverity.HIGH,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.of(2026, 11, 9, 10, 0),
                service
        );

        createIncident(
                "Medium severity incident",
                IncidentSeverity.MEDIUM,
                IncidentStatus.OPEN,
                "PRODUCTION",
                LocalDateTime.of(2026, 11, 8, 10, 0),
                service
        );

        Page<Incident> result = incidentRepository.findAll(
                PageRequest.of(
                        0,
                        2,
                        Sort.by(Sort.Direction.DESC, "startedAt")
                )
        );

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());

        assertEquals(
                "High severity incident",
                result.getContent().get(0).getTitle()
        );
    }
}