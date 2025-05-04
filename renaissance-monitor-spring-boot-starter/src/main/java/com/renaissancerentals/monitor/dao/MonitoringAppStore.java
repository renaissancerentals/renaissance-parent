package com.renaissancerentals.monitor.dao;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import com.renaissancerentals.monitor.model.MonitoringApp;

@Component
public class MonitoringAppStore {
    private final ConcurrentHashMap<String, MonitoringApp> monitoringApps = new ConcurrentHashMap<>();

    public MonitoringApp get(final String name){
        return monitoringApps.get(name);
    }

    public Collection<MonitoringApp> getAll(){
        return monitoringApps.values();
    }

    public void compute(String name,BiFunction<String, MonitoringApp, MonitoringApp> remappingFunction){
        monitoringApps.compute(name,remappingFunction);
    }

    public void putAll(final Map<String, MonitoringApp> apps){
        monitoringApps.putAll(apps);
    }
}
