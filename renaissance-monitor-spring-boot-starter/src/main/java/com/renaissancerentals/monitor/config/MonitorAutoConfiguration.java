package com.renaissancerentals.monitor.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.monitor.controller.MonitorController;
import com.renaissancerentals.monitor.dao.MonitoringAppStore;
import com.renaissancerentals.monitor.external.HealthService;
import com.renaissancerentals.monitor.mapper.MonitorAppMapper;
import com.renaissancerentals.monitor.service.MonitorMailService;
import com.renaissancerentals.monitor.service.MonitorService;
import com.renaissancerentals.monitor.template.MonitorErrorMailTemplate;
import com.renaissancerentals.monitor.template.MonitorHealthyMailTemplate;

@Configuration
@ImportAutoConfiguration(classes = {MonitorController.class, MonitorConfiguration.class, MonitoringAppStore.class,
        HealthService.class, MonitorAppMapper.class, MonitorMailService.class, MonitorService.class,
        MonitorErrorMailTemplate.class, MonitorHealthyMailTemplate.class})
public class MonitorAutoConfiguration {

}
