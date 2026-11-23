package com.aditya.pulseforge.controller;

import com.aditya.pulseforge.dto.MonitoredServiceRequest;
import com.aditya.pulseforge.dto.MonitoredServiceResponse;
import com.aditya.pulseforge.service.MonitoredServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class MonitoredServiceController {

    private final MonitoredServiceService service;

    public MonitoredServiceController(
            MonitoredServiceService service) {

        this.service = service;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MonitoredServiceResponse> createService(
            @Valid @RequestBody MonitoredServiceRequest request) {

        return ResponseEntity.ok(
                service.createService(request)
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MonitoredServiceResponse> getServiceById(
            @PathVariable Integer id) {

        return service.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    // GET ALL
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MonitoredServiceResponse>> getAllServices() {

        return ResponseEntity.ok(
                service.getAllServices()
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MonitoredServiceResponse> updateService(
            @PathVariable Integer id,
            @Valid @RequestBody MonitoredServiceRequest request) {

        return service.updateService(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteService(
            @PathVariable Integer id) {

        boolean deleted =
                service.deleteService(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}