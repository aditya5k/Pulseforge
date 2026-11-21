package com.aditya.pulseforge.dto;

import com.aditya.pulseforge.enums.IncidentSeverity;
import com.aditya.pulseforge.enums.IncidentStatus;

import java.time.LocalDateTime;

public class IncidentResponse {

    private Integer id;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private IncidentStatus status;
    private String environment;
    private LocalDateTime startedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;

    private Integer serviceId;
    private String serviceName;

    public IncidentResponse() {
    }

    public IncidentResponse(
            Integer id,
            String title,
            String description,
            IncidentSeverity severity,
            IncidentStatus status,
            String environment,
            LocalDateTime startedAt,
            LocalDateTime resolvedAt,
            LocalDateTime createdAt,
            Integer serviceId,
            String serviceName) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.environment = environment;
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
        this.createdAt = createdAt;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}