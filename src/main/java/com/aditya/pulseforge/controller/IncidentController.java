
package com.aditya.pulseforge.controller;

import com.aditya.pulseforge.dto.IncidentRequest;
import com.aditya.pulseforge.dto.IncidentResponse;
import com.aditya.pulseforge.enums.IncidentSeverity;
import com.aditya.pulseforge.enums.IncidentStatus;
import com.aditya.pulseforge.service.IncidentService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // CREATE INCIDENT
    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody IncidentRequest request) {

        IncidentResponse response =
                incidentService.createIncident(request);

        return ResponseEntity.status(201).body(response);
    }

    // GET ALL INCIDENTS WITH FILTERING, PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<IncidentResponse>> getAllIncidents(
            @RequestParam(required = false) IncidentSeverity severity,
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) String environment,
            @RequestParam(required = false) Integer serviceId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return ResponseEntity.ok(
                incidentService.getAllIncidents(
                        severity,
                        status,
                        environment,
                        serviceId,
                        pageable));
    }

    // GET INCIDENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                incidentService.getIncidentById(id));
    }

    // UPDATE INCIDENT
    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponse> updateIncident(
            @PathVariable Integer id,
            @Valid @RequestBody IncidentRequest request) {

        return ResponseEntity.ok(
                incidentService.updateIncident(id, request));
    }

    // DELETE INCIDENT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(
            @PathVariable Integer id) {

        incidentService.deleteIncident(id);

        return ResponseEntity.noContent().build();
    }
}
