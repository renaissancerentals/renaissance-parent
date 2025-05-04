package com.renaissancerentals.monitor.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.renaissancerentals.monitor.external.model.HealthResponse;
import com.renaissancerentals.monitor.model.HealthStatus;
import com.renaissancerentals.monitor.model.MonitoringApp;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthService {
    private final RestClient restClient;

    public HealthStatus fetchHealth(final MonitoringApp app){
        final var healthResponse = restClient.get().uri(app.healthEndpoint()).retrieve().body(HealthResponse.class);

        if (healthResponse == null)
            return HealthStatus.DOWN;

        return healthResponse.status();
    }

}
