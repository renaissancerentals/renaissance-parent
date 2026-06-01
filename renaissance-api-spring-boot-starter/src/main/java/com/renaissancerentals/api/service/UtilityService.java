package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.Utility;
import com.renaissancerentals.api.domain.mapper.UtilityMapper;
import com.renaissancerentals.persistence.dao.UtilityDao;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilityService {

    private final UtilityDao utilityDao;
    private final UtilityMapper utilityMapper;
    private final ExecutorService virtualThreadExecutor;

    public CompletableFuture<List<Utility>> getUtilitiesForFloorplanAsync(String floorplanId) {
        return CompletableFuture.supplyAsync(
                () -> utilityDao.findByFloorplanId(floorplanId).stream()
                        .map(utilityMapper::toDomain)
                        .toList(),
                virtualThreadExecutor);
    }
}
