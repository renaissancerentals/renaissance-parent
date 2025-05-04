package com.renaissancerentals.monitor.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.renaissancerentals.monitor.config.MonitorConfigProperties;
import com.renaissancerentals.monitor.model.HealthMetadata;
import com.renaissancerentals.monitor.model.MonitoringApp;
import com.renaissancerentals.monitor.template.model.MonitorErrorMessage;
import com.renaissancerentals.monitor.template.model.MonitorHealthyMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MonitorAppMapper {

    private static final DateTimeFormatter LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final MonitorConfigProperties configProperties;

    public MonitorErrorMessage mapToErrorMessage(MonitoringApp app){
        return MonitorErrorMessage.builder().applicationName(app.name()).status(app.health().status())
                .healthEndpoint(app.healthEndpoint()).retryCount(app.health().retryCounter())
                .time(ZonedDateTime.now(ZoneId.of(configProperties.monitoringZoneId())).format(LOCAL_DATE_TIME))
                .errorSince(app.health().errorSince().format(LOCAL_DATE_TIME)).errorMessage(app.health().errorMessage())
                .errorType(app.health().errorType()).build();
    }

    public MonitorHealthyMessage mapToHealthyMessage(MonitoringApp app,HealthMetadata previousHealth){
        final var now = LocalDateTime.now(ZoneId.of(configProperties.monitoringZoneId()));

        return MonitorHealthyMessage.builder().applicationName(app.name()).previousStatus(previousHealth.status())
                .healthEndpoint(app.healthEndpoint()).time(now.format(LOCAL_DATE_TIME))
                .downtimeDuration(getDowntimeDuration(previousHealth.errorSince(),now)).build();
    }

    private String getDowntimeDuration(LocalDateTime errorSince,LocalDateTime now){
        final var duration = Duration.between(errorSince,now);

        final long seconds = duration.getSeconds();
        final long hours = seconds / 3600;
        final long minutes = (seconds % 3600) / 60;
        final long remainingSeconds = seconds % 60;

        final StringBuilder sb = new StringBuilder();
        if (hours > 0)
            sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        if (minutes > 0)
            sb.append(minutes).append(" minute").append(minutes > 1 ? "s " : " ");
        if (hours == 0 && minutes == 0)
            sb.append(remainingSeconds).append(" second").append(remainingSeconds > 1 ? "s" : "");

        return sb.toString().trim();
    }
}
