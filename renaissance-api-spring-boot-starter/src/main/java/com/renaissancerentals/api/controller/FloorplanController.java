package com.renaissancerentals.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.api.domain.projection.Projection;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.FloorplanRepository;
import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/floorplans")
@RequiredArgsConstructor
public class FloorplanController {
    private final FloorplanRepository floorplanRepository;

    @GetMapping("/{floorplanId}")
    public ResponseEntity<?> get(@PathVariable("floorplanId") String floorplanId,
            @RequestParam(value = "projection") Projection projection){
        switch (projection) {
            case DETAILS -> {
                return ResponseEntity.ok(floorplanRepository.getFloorplan(floorplanId).orElseThrow(
                        () -> new NotFoundException(String.format("Floorplan with id: %s not found.",floorplanId))));
            }
            case SPOTLIGHT -> {
                return ResponseEntity.ok(floorplanRepository.getFloorplanSpotlight(floorplanId).orElseThrow(
                        () -> new NotFoundException(String.format("Floorplan with id: %s not found.",floorplanId))));
            }
            default -> throw new NotFoundException(String.format("Floorplan with id: %s not found.",floorplanId));
        }
    }

    @GetMapping("/byPropertyId/{propertyId}")
    public ResponseEntity<?> getFloorplansBy(@PathVariable("propertyId") String propertyId,
            @RequestParam(value = "projection") Projection projection){
        if (Projection.DETAILS.equals(projection)) {
            return ResponseEntity.ok(floorplanRepository.getFloorplansForProperty(propertyId));
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }

    }

    @GetMapping
    public ResponseEntity<?> getFloorplans(@RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "projection") Projection projection){
        switch (projection) {
            case SPOTLIGHT -> {
                if ("featured".equals(filterBy)) {
                    return ResponseEntity.ok(floorplanRepository.getFeaturedSpotlights());
                } else {
                    throw new ClientException(ErrorMessage.builder().message("Unsupported param").build());
                }
            }
            case DETAILS -> {
                return ResponseEntity.ok(floorplanRepository.getActiveFloorplansDetails());
            }
            default -> throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }

}
