package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.mapper.PropertyMapper;
import com.renaissancerentals.api.domain.projection.LeasingOfficeDetails;
import com.renaissancerentals.api.domain.projection.PropertyLeasingOffice;
import com.renaissancerentals.persistence.dao.LeasingOfficeDao;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.entity.LeasingOfficeEntity;
import com.renaissancerentals.persistence.entity.PropertyEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeasingOfficeService {
    private final PropertyMapper propertyMapper;
    private final PropertyDao propertyDao;
    private final LeasingOfficeDao leasingOfficeDao;
    private final ExecutorService virtualThreadExecutor;

    public List<LeasingOfficeDetails> getAllLeasingOffices() {
        var leasingOfficesFuture = getLeasingOfficesAsync();
        var propertiesFuture = getPropertiesAsync();

        var leasingOffices = leasingOfficesFuture.join();
        var properties = propertiesFuture.join();

        CompletableFuture.allOf(leasingOfficesFuture, propertiesFuture).join();

        Map<String, List<PropertyLeasingOffice>> leasingOfficeProperties = new HashMap<>();
        properties.forEach(property -> {
            if (property.getLeasingOfficeId() != null) {
                leasingOfficeProperties.putIfAbsent(property.getLeasingOfficeId(), new ArrayList<>());
                leasingOfficeProperties
                        .get(property.getLeasingOfficeId())
                        .add(propertyMapper.toPropertyLeasingOffice(property));
            }
        });

        return leasingOffices.stream()
                .map(entity -> LeasingOfficeDetails.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .address(entity.getAddress())
                        .zipcode(entity.getZipcode())
                        .phone(entity.getPhone())
                        .officeHours(entity.getOfficeHours())
                        .direction(entity.getDirection())
                        .officeMap(entity.getOfficeMap())
                        .officeMapLandscape(entity.getOfficeMapLandscape())
                        .officeImage(entity.getOfficeImage())
                        .officeImageDescription(entity.getOfficeImageDescription())
                        .properties(leasingOfficeProperties.getOrDefault(entity.getId(), List.of()))
                        .build())
                .toList();
    }

    private CompletableFuture<List<LeasingOfficeEntity>> getLeasingOfficesAsync() {
        return CompletableFuture.supplyAsync(
                () -> StreamSupport.stream(leasingOfficeDao.findAll().spliterator(), false)
                        .toList(),
                virtualThreadExecutor);
    }

    private CompletableFuture<List<PropertyEntity>> getPropertiesAsync() {
        return CompletableFuture.supplyAsync(
                () -> StreamSupport.stream(propertyDao.findAll().spliterator(), false)
                        .toList(),
                virtualThreadExecutor);
    }
}
