package com.renaissancerentals.monitor.controller;

import com.renaissancerentals.monitor.model.MonitoringApp;
import com.renaissancerentals.monitor.service.MonitorService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Slf4j
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping("/{applicationName}")
    public ResponseEntity<MonitoringApp> status(@PathVariable("applicationName") String applicationName) {
        return ResponseEntity.ok(monitorService.getApplication(applicationName));
    }

    @GetMapping
    public ResponseEntity<Collection<MonitoringApp>> allStatus() {
        return ResponseEntity.ok(monitorService.getApplications());
    }

    @PostMapping
    @Scheduled(fixedRateString = "${renaissancerentals.monitor.run-in-every-milliseconds}", initialDelay = 2000)
    public ResponseEntity<Void> updateStatuses() {
        log.debug("Running scheduled health check");
        monitorService.checkHealth();
        return ResponseEntity.noContent().build();
    }
}
