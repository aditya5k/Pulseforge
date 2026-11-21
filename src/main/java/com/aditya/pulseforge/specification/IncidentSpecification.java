
package com.aditya.pulseforge.specification;

import com.aditya.pulseforge.entity.Incident;
import com.aditya.pulseforge.enums.IncidentSeverity;
import com.aditya.pulseforge.enums.IncidentStatus;

import org.springframework.data.jpa.domain.Specification;

public class IncidentSpecification {

    private IncidentSpecification() {
    }

    public static Specification<Incident> hasSeverity(
            IncidentSeverity severity) {

        return (root, query, criteriaBuilder) ->
                severity == null ? null :
                        criteriaBuilder.equal(
                                root.get("severity"), severity);
    }

    public static Specification<Incident> hasStatus(
            IncidentStatus status) {

        return (root, query, criteriaBuilder) ->
                status == null ? null :
                        criteriaBuilder.equal(
                                root.get("status"), status);
    }

    public static Specification<Incident> hasEnvironment(
            String environment) {

        return (root, query, criteriaBuilder) ->
                environment == null || environment.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                        criteriaBuilder.lower(
                                root.get("environment")),
                        environment.trim().toLowerCase());
    }

    public static Specification<Incident> hasServiceId(
            Integer serviceId) {

        return (root, query, criteriaBuilder) ->
                serviceId == null ? null :
                        criteriaBuilder.equal(
                                root.get("service").get("id"),
                                serviceId);
    }
}
