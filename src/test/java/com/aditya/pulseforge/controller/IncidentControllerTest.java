package com.aditya.pulseforge.controller;

import com.aditya.pulseforge.dto.IncidentRequest;
import com.aditya.pulseforge.dto.IncidentResponse;
import com.aditya.pulseforge.enums.IncidentSeverity;
import com.aditya.pulseforge.enums.IncidentStatus;
import com.aditya.pulseforge.exception.GlobalExceptionHandler;
import com.aditya.pulseforge.exception.ResourceNotFoundException;
import com.aditya.pulseforge.security.CustomUserDetailsService;
import com.aditya.pulseforge.security.JwtService;
import com.aditya.pulseforge.service.IncidentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
@AutoConfigureMockMvc(addFilters = false)   // controller logic only; security is tested in SecurityIntegrationTest
@Import(GlobalExceptionHandler.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService incidentService;

    // Needed so JwtAuthenticationFilter (a @Component) can be created in the test context
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Common test response
    private IncidentResponse createIncidentResponse() {

        IncidentResponse response = new IncidentResponse();

        response.setId(10);
        response.setTitle("Payment API failure");
        response.setDescription("Payment service is not responding");
        response.setSeverity(IncidentSeverity.HIGH);
        response.setStatus(IncidentStatus.OPEN);
        response.setEnvironment("PRODUCTION");
        response.setStartedAt(LocalDateTime.of(2026, 11, 9, 10, 30));
        response.setServiceId(1);
        response.setServiceName("Payment Service");

        return response;
    }

    // CREATE INCIDENT

    @Test
    void createIncident_shouldReturnCreatedResponse() throws Exception {

        IncidentResponse response = createIncidentResponse();

        when(incidentService.createIncident(any(IncidentRequest.class)))
                .thenReturn(response);

        String requestJson = """
                {
                    "title": "Payment API failure",
                    "description": "Payment service is not responding",
                    "severity": "HIGH",
                    "status": "OPEN",
                    "environment": "PRODUCTION",
                    "startedAt": "2026-11-09T10:30:00",
                    "serviceId": 1
                }
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title")
                        .value("Payment API failure"))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.serviceId").value(1))
                .andExpect(jsonPath("$.serviceName")
                        .value("Payment Service"));

        verify(incidentService).createIncident(
                any(IncidentRequest.class));
    }

    // GET ALL INCIDENTS

    @Test
    void getAllIncidents_shouldReturnIncidentPage() throws Exception {

        IncidentResponse response = createIncidentResponse();

        Page<IncidentResponse> incidentPage =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 10),
                        1
                );

        when(incidentService.getAllIncidents(
                eq(IncidentSeverity.HIGH),
                eq(IncidentStatus.OPEN),
                eq("PRODUCTION"),
                eq(1),
                any(Pageable.class)
        )).thenReturn(incidentPage);

        mockMvc.perform(get("/api/incidents")
                        .param("severity", "HIGH")
                        .param("status", "OPEN")
                        .param("environment", "PRODUCTION")
                        .param("serviceId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Payment API failure"));

        verify(incidentService).getAllIncidents(
                eq(IncidentSeverity.HIGH),
                eq(IncidentStatus.OPEN),
                eq("PRODUCTION"),
                eq(1),
                any(Pageable.class)
        );
    }

    // GET INCIDENT BY ID

    @Test
    void getIncidentById_shouldReturnIncident() throws Exception {

        IncidentResponse response = createIncidentResponse();

        when(incidentService.getIncidentById(10))
                .thenReturn(response);

        mockMvc.perform(get("/api/incidents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title")
                        .value("Payment API failure"))
                .andExpect(jsonPath("$.serviceName")
                        .value("Payment Service"));

        verify(incidentService).getIncidentById(10);
    }

    // UPDATE INCIDENT

    @Test
    void updateIncident_shouldReturnUpdatedIncident() throws Exception {

        IncidentResponse response = createIncidentResponse();

        when(incidentService.updateIncident(
                eq(10),
                any(IncidentRequest.class)
        )).thenReturn(response);

        String requestJson = """
                {
                    "title": "Updated Payment API failure",
                    "description": "Payment service recovered",
                    "severity": "HIGH",
                    "status": "INVESTIGATING",
                    "environment": "PRODUCTION",
                    "startedAt": "2026-11-09T10:30:00",
                    "serviceId": 1
                }
                """;

        mockMvc.perform(put("/api/incidents/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title")
                        .value("Payment API failure"));

        verify(incidentService).updateIncident(
                eq(10),
                any(IncidentRequest.class)
        );
    }

    // DELETE INCIDENT

    @Test
    void deleteIncident_shouldReturnNoContent() throws Exception {

        doNothing().when(incidentService).deleteIncident(10);

        mockMvc.perform(delete("/api/incidents/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(incidentService).deleteIncident(10);
    }

    // VALIDATION FAILURE

    @Test
    void createIncident_shouldReturnBadRequestForInvalidRequest()
            throws Exception {

        String invalidRequestJson = """
                {
                    "title": "",
                    "description": "Payment service is not responding",
                    "severity": "HIGH",
                    "status": "OPEN",
                    "environment": "PRODUCTION",
                    "startedAt": "2026-11-09T10:30:00",
                    "serviceId": 1
                }
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());

        verify(incidentService, never())
                .createIncident(any(IncidentRequest.class));
    }

    // INCIDENT NOT FOUND

    @Test
    void getIncidentById_shouldReturnNotFound() throws Exception {

        when(incidentService.getIncidentById(999))
                .thenThrow(new ResourceNotFoundException(
                        "Incident not found with id: 999"));

        mockMvc.perform(get("/api/incidents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Incident not found with id: 999"));

        verify(incidentService).getIncidentById(999);
    }
}