package com.renaissancerentals.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.api.domain.projection.Projection;
import com.renaissancerentals.api.repository.UnitRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {
    private final UnitRepository unitRepository;
    private final FloorplanService floorplanService;

    @GetMapping
    public ResponseEntity<List<?>> getAll(@RequestParam(value = "projection") Projection projection){
        if (Projection.ADDRESS.equals(projection)) {
            return ResponseEntity.ok(unitRepository.getAllAddresses());
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<?> get(@PathVariable("unitId") String unitId,
            @RequestParam(value = "projection") Projection projection){
        return switch (projection) {
            case UTILITIES -> ResponseEntity.ok(unitRepository.getUnitUtilities(unitId));
            case UNIT_FLOORPLAN -> ResponseEntity.ok(floorplanService.getUnitFloorplan(unitId));
            default -> throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        };
    }
}
