package com.renaissancerentals.monitor.config;

import com.renaissancerentals.monitor.dao.MonitoringAppStore;
import com.renaissancerentals.monitor.model.HealthMetadata;
import com.renaissancerentals.monitor.model.HealthStatus;
import com.renaissancerentals.monitor.model.MonitoringApp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(MonitorConfigProperties.class)
public class MonitorConfiguration {
    @Bean
    public ApplicationRunner preloadApplications(
            MonitorConfigProperties configProperties, MonitoringAppStore appStore) {
        return args -> {
            final var apps = configProperties.applications().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> MonitoringApp.builder()
                            .name(e.getKey())
                            .healthEndpoint(e.getValue().healthEndpoint())
                            .health(HealthMetadata.builder()
                                    .status(HealthStatus.UP)
                                    .updatedAt(LocalDateTime.now(ZoneId.of(configProperties.monitoringZoneId())))
                                    .build())
                            .build()));
            appStore.putAll(apps);
        };
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
