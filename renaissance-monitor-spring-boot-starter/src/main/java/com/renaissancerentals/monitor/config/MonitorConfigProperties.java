package com.renaissancerentals.monitor.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.monitor")
public record MonitorConfigProperties(Integer runInEveryMilliseconds, Integer retry, Integer timeoutInSeconds,
        String sendNotificationEmailTo, Map<String, Application> applications, String monitoringZoneId) {
    public MonitorConfigProperties {
        applications = applications == null ? Map.of() : Map.copyOf(applications);
    }

    public record Application(String healthEndpoint) {

    }
}
