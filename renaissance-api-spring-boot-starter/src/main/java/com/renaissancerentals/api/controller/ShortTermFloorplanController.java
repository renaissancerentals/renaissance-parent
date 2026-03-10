package com.renaissancerentals.api.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.api.domain.projection.Projection;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.service.ShortTermService;
import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shortTermFloorplans")
@RequiredArgsConstructor
public class ShortTermFloorplanController {
    private final ShortTermService shortTermService;

    @GetMapping("/{floorplanId}")
    public ResponseEntity<?> get(@PathVariable("floorplanId") String floorplanId,
            @RequestParam(value = "projection") Projection projection){
        if (Objects.requireNonNull(projection) == Projection.DETAILS) {
            return ResponseEntity.ok(shortTermService.getShortTermFloorplan(floorplanId));
        }
        throw new NotFoundException(String.format("Floorplan with id: %s not found.",floorplanId));
    }

    @GetMapping("/byPropertyId/{propertyId}")
    public ResponseEntity<?> getShortTermsBy(@PathVariable("propertyId") String propertyId,
            @RequestParam(value = "projection") Projection projection){
        if (Projection.DETAILS.equals(projection)) {
            return ResponseEntity.ok(shortTermService.getShortTermFloorplansForProperty(propertyId));
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }

}
