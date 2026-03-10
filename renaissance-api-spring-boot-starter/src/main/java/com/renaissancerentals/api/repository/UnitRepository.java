package com.renaissancerentals.api.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.renaissancerentals.api.domain.Utility;
import com.renaissancerentals.api.domain.mapper.UnitMapper;
import com.renaissancerentals.api.domain.projection.UnitAddress;
import com.renaissancerentals.api.domain.projection.UnitUtilities;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.persistence.dao.FloorplanDao;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.dao.UnitDao;
import com.renaissancerentals.persistence.dao.UtilityDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UnitRepository {

    private final UnitDao unitDao;
    private final UnitMapper unitMapper;
    private final UtilityDao utilityRepository;
    private final FloorplanDao floorplanRepository;
    private final PropertyDao propertyRepository;

    public List<UnitAddress> getAllAddresses(){
        return unitDao.findAllActive().stream().map(unitMapper::toAddress).toList();
    }

    public UnitUtilities getUnitUtilities(String unitId){

        var unit = unitDao.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found"));

        var floorplan = floorplanRepository.findById(unit.getFloorplanId())
                .orElseThrow(() -> new NotFoundException("Floorplan not found"));

        var property = propertyRepository.findById(floorplan.getPropertyId())
                .orElseThrow(() -> new NotFoundException("Property not found"));

        var utilities = utilityRepository.findByFloorplanId(floorplan.getId()).stream()
                .map(utilityEntity -> Utility.builder().id(utilityEntity.getId()).name(utilityEntity.getName())
                        .type(utilityEntity.getType()).averageMonthlyBill(utilityEntity.getAverageMonthlyBill())
                        .build())
                .toList();
        return UnitUtilities.builder().id(unit.getId()).utilities(utilities).propertyEmail(property.getEmail()).build();

    }
}
