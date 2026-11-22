package com.aditya.pulseforge.repository;

import com.aditya.pulseforge.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncidentRepository extends
        JpaRepository<Incident, Integer>,
        JpaSpecificationExecutor<Incident> {

    // Fixes N+1: load each incident's service in the same query (LEFT JOIN)
    // instead of one extra SELECT per incident.
    @Override
    @EntityGraph(attributePaths = "service")
    Page<Incident> findAll(Specification<Incident> spec, Pageable pageable);

    // Derived query: SELECT COUNT(*) > 0 FROM incidents WHERE service_id = ?
    boolean existsByService_Id(Integer serviceId);
}
