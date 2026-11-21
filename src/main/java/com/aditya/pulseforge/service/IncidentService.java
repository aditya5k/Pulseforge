
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
import com.aditya.pulseforge.specification.IncidentSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final IncidentMapper incidentMapper;

    public IncidentService(
            IncidentRepository incidentRepository,
            MonitoredServiceRepository monitoredServiceRepository,
            IncidentMapper incidentMapper) {

        this.incidentRepository = incidentRepository;
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.incidentMapper = incidentMapper;
    }

    // CREATE INCIDENT
    public IncidentResponse createIncident(IncidentRequest request) {

        MonitoredService service =
                monitoredServiceRepository.findById(request.getServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Monitored service not found with id: "
                                                + request.getServiceId()));

        Incident incident = incidentMapper.toEntity(request, service);
        Incident savedIncident = incidentRepository.save(incident);

        return incidentMapper.toResponse(savedIncident);
    }

    // GET ALL INCIDENTS WITH FILTERING AND PAGINATION
    public Page<IncidentResponse> getAllIncidents(
            IncidentSeverity severity,
            IncidentStatus status,
            String environment,
            Integer serviceId,
            Pageable pageable) {

        Specification<Incident> specification =
                Specification.where(
                                IncidentSpecification.hasSeverity(severity))
                        .and(IncidentSpecification.hasStatus(status))
                        .and(IncidentSpecification.hasEnvironment(environment))
                        .and(IncidentSpecification.hasServiceId(serviceId));

        return incidentRepository.findAll(specification, pageable)
                .map(incidentMapper::toResponse);
    }

    // GET INCIDENT BY ID
    public IncidentResponse getIncidentById(Integer id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found with id: " + id));

        return incidentMapper.toResponse(incident);
    }

    // UPDATE INCIDENT
    public IncidentResponse updateIncident(
            Integer id,
            IncidentRequest request) {

        Incident existingIncident = incidentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found with id: " + id));

        MonitoredService service =
                monitoredServiceRepository.findById(request.getServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Monitored service not found with id: "
                                                + request.getServiceId()));

        incidentMapper.updateEntity(existingIncident, request, service);

        Incident updatedIncident =
                incidentRepository.save(existingIncident);

        return incidentMapper.toResponse(updatedIncident);
    }

    // DELETE INCIDENT
    public void deleteIncident(Integer id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found with id: " + id));

        incidentRepository.delete(incident);
    }
}
