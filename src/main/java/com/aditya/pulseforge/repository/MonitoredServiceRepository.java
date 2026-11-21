package com.aditya.pulseforge.repository;

import com.aditya.pulseforge.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoredServiceRepository
        extends JpaRepository<MonitoredService, Integer> {
}