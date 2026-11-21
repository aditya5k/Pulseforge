package com.aditya.pulseforge.dto;

import java.time.LocalDateTime;

public class MonitoredServiceResponse {

    private Integer id;
    private String name;
    private String description;
    private String teamName;
    private String environment;
    private String repositoryUrl;
    private LocalDateTime createdAt;

    public MonitoredServiceResponse() {
    }

    public MonitoredServiceResponse(
            Integer id,
            String name,
            String description,
            String teamName,
            String environment,
            String repositoryUrl,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.teamName = teamName;
        this.environment = environment;
        this.repositoryUrl = repositoryUrl;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}