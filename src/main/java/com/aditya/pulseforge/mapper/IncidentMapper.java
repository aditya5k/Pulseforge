package com.aditya.pulseforge.mapper;

import com.aditya.pulseforge.dto.IncidentRequest;
import com.aditya.pulseforge.dto.IncidentResponse;
import com.aditya.pulseforge.entity.Incident;
import com.aditya.pulseforge.entity.MonitoredService;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    // Request DTO → Entity
    public Incident toEntity(
            IncidentRequest request,
            MonitoredService service) {

        Incident incident = new Incident();

        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setStatus(request.getStatus());
        incident.setEnvironment(request.getEnvironment());
        incident.setStartedAt(request.getStartedAt());
        incident.setResolvedAt(request.getResolvedAt());
        incident.setService(service);

        return incident;
    }

    // Entity → Response DTO
    public IncidentResponse toResponse(
            Incident incident) {

        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getEnvironment(),
                incident.getStartedAt(),
                incident.getResolvedAt(),
                incident.getCreatedAt(),
                incident.getService().getId(),
                incident.getService().getName()
        );
    }

    // Request DTO → Existing Entity
    public void updateEntity(
            Incident incident,
            IncidentRequest request,
            MonitoredService service) {

        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setStatus(request.getStatus());
        incident.setEnvironment(request.getEnvironment());
        incident.setStartedAt(request.getStartedAt());
        incident.setResolvedAt(request.getResolvedAt());
        incident.setService(service);
    }
}