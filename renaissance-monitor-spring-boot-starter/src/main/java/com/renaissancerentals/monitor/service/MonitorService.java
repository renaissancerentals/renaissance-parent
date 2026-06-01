package com.renaissancerentals.monitor.service;

import com.renaissancerentals.monitor.config.MonitorConfigProperties;
import com.renaissancerentals.monitor.dao.MonitoringAppStore;
import com.renaissancerentals.monitor.external.HealthService;
import com.renaissancerentals.monitor.mapper.MonitorAppMapper;
import com.renaissancerentals.monitor.model.HealthMetadata;
import com.renaissancerentals.monitor.model.HealthStatus;
import com.renaissancerentals.monitor.model.MonitoringApp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitorService {
    private final HealthService healthService;
    private final MonitorMailService mailService;
    private final MonitorAppMapper mapper;
    private final MonitoringAppStore appStore;
    private final MonitorConfigProperties configProperties;

    public void checkHealth() {
        appStore.getAll().forEach(this::checkAppHealth);
    }

    public MonitoringApp getApplication(String applicationName) {
        return appStore.get(applicationName);
    }

    public Collection<MonitoringApp> getApplications() {
        return appStore.getAll();
    }

    private void checkAppHealth(MonitoringApp app) {
        try {
            if (healthService.fetchHealth(app).equals(HealthStatus.UP)) {
                updateAppHealth(app, buildHealthyMetadata());
                return;
            }
        } catch (Exception e) {
            updateAppHealth(app, buildErrorMetadata(app, e));
            return;
        }
        updateAppHealth(app, buildErrorMetadata(app, null));
    }

    private HealthMetadata buildHealthyMetadata() {
        return HealthMetadata.builder().status(HealthStatus.UP).updatedAt(now()).build();
    }

    private HealthMetadata buildErrorMetadata(MonitoringApp app, Exception e) {
        var prev = app.health();
        var errorSince = Optional.ofNullable(prev.errorSince()).orElse(now());

        return HealthMetadata.builder()
                .status(HealthStatus.DOWN)
                .updatedAt(now())
                .retryCounter(prev.retryCounter() + 1)
                .errorSince(errorSince)
                .errorMessage(Optional.ofNullable(e).map(Throwable::getMessage).orElse("Unknown"))
                .errorType(Optional.ofNullable(e)
                        .map(t -> t.getClass().getSimpleName())
                        .orElse("Unknown"))
                .notificationSent(true)
                .build();
    }

    private void updateAppHealth(MonitoringApp app, HealthMetadata metadata) {
        appStore.compute(app.name(), (name, existingApp) -> {
            var updatedApp = MonitoringApp.builder()
                    .name(existingApp.name())
                    .healthEndpoint(existingApp.healthEndpoint())
                    .health(metadata)
                    .build();

            if (metadata.status() == HealthStatus.DOWN && !existingApp.health().notificationSent()) {
                mailService.sendErrorMail(mapper.mapToErrorMessage(updatedApp));
            } else if (metadata.status() == HealthStatus.UP
                    && existingApp.health().status() == HealthStatus.DOWN) {
                mailService.sendHealthyMail(mapper.mapToHealthyMessage(updatedApp, existingApp.health()));
            }

            return updatedApp;
        });
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(configProperties.monitoringZoneId()));
    }
}
