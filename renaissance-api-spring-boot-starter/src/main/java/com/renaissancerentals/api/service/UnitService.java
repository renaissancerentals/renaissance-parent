package com.renaissancerentals.api.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Service;

import com.renaissancerentals.api.domain.Unit;
import com.renaissancerentals.api.domain.mapper.UnitMapper;
import com.renaissancerentals.persistence.dao.UnitDao;
import com.renaissancerentals.persistence.entity.UnitEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UnitService {

    private final UnitMapper unitMapper;
    private final UnitDao unitDao;
    private final ExecutorService virtualThreadExecutor;

    public CompletableFuture<List<Unit>> getUnitsForFloorplanAsync(String floorplanId){
        return CompletableFuture.supplyAsync(
                () -> unitDao.findAllByFloorplanId(floorplanId).stream().map(unitMapper::toDomain).toList(),
                virtualThreadExecutor);
    }

    public Optional<UnitEntity> getUnit(String unitId){
        return unitDao.findById(unitId);
    }
}
