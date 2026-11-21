package com.aditya.pulseforge.mapper;

import com.aditya.pulseforge.dto.MonitoredServiceRequest;
import com.aditya.pulseforge.dto.MonitoredServiceResponse;
import com.aditya.pulseforge.entity.MonitoredService;
import org.springframework.stereotype.Component;

@Component
public class MonitoredServiceMapper {

    // Request DTO → Entity
    public MonitoredService toEntity(MonitoredServiceRequest request) {

        MonitoredService service = new MonitoredService();

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setTeamName(request.getTeamName());
        service.setEnvironment(request.getEnvironment());
        service.setRepositoryUrl(request.getRepositoryUrl());

        return service;
    }

    // Entity → Response DTO
    public MonitoredServiceResponse toResponse(
            MonitoredService service) {

        return new MonitoredServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getTeamName(),
                service.getEnvironment(),
                service.getRepositoryUrl(),
                service.getCreatedAt()
        );
    }

    // Request DTO → Existing Entity
    public void updateEntity(
            MonitoredService service,
            MonitoredServiceRequest request) {

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setTeamName(request.getTeamName());
        service.setEnvironment(request.getEnvironment());
        service.setRepositoryUrl(request.getRepositoryUrl());
    }
}