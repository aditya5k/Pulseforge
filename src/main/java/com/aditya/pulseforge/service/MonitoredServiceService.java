package com.aditya.pulseforge.service;

import com.aditya.pulseforge.dto.MonitoredServiceRequest;
import com.aditya.pulseforge.dto.MonitoredServiceResponse;
import com.aditya.pulseforge.entity.MonitoredService;
import com.aditya.pulseforge.mapper.MonitoredServiceMapper;
import com.aditya.pulseforge.repository.MonitoredServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonitoredServiceService {

    private final MonitoredServiceRepository repository;
    private final MonitoredServiceMapper mapper;

    public MonitoredServiceService(
            MonitoredServiceRepository repository,
            MonitoredServiceMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    public MonitoredServiceResponse createService(
            MonitoredServiceRequest request) {

        MonitoredService service =
                mapper.toEntity(request);

        MonitoredService savedService =
                repository.save(service);

        return mapper.toResponse(savedService);
    }

    // GET ALL
    public List<MonitoredServiceResponse> getAllServices() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // GET BY ID
    public Optional<MonitoredServiceResponse> getServiceById(
            Integer id) {

        return repository.findById(id)
                .map(mapper::toResponse);
    }

    // UPDATE
    public Optional<MonitoredServiceResponse> updateService(
            Integer id,
            MonitoredServiceRequest request) {

        return repository.findById(id)
                .map(existingService -> {

                    mapper.updateEntity(
                            existingService,
                            request
                    );

                    MonitoredService updatedService =
                            repository.save(existingService);

                    return mapper.toResponse(updatedService);
                });
    }

    // DELETE
    public boolean deleteService(Integer id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}