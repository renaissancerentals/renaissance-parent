package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.mapper.AmenityMapper;
import com.renaissancerentals.persistence.dao.AmenityDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityDao amenityDao;
    private final AmenityMapper amenityMapper;
    private final ExecutorService virtualThreadExecutor;

    public CompletableFuture<List<Amenity>> getAmenitiesForFloorplanAsync(String floorplanId) {
        return CompletableFuture.supplyAsync(() ->
                        amenityDao.findByFloorplanId(floorplanId).stream()
                                .map(amenityMapper::toDomain).toList()
                , virtualThreadExecutor);
    }
}
