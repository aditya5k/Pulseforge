
package com.aditya.pulseforge.repository;

import com.aditya.pulseforge.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncidentRepository extends
        JpaRepository<Incident, Integer>,
        JpaSpecificationExecutor<Incident> {
}
