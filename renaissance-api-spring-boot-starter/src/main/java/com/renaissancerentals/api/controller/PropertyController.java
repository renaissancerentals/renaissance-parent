package com.renaissancerentals.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.projection.Projection;
import com.renaissancerentals.api.service.PropertyService;
import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping("/{propertyId}")
    public ResponseEntity<?> get(@PathVariable("propertyId") String propertyId,
            @RequestParam(value = "projection") Projection projection){
        if (Projection.DETAILS.equals(projection)) {
            return ResponseEntity.ok(propertyService.getProperty(propertyId));
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }

    }

    @GetMapping("/{propertyId}/floorplans")
    public ResponseEntity<?> getFloorplans(@PathVariable("propertyId") String propertyId,
            @RequestParam(value = "projection") Projection projection){
        if (Projection.FILTER.equals(projection)) {
            return ResponseEntity.ok(propertyService.getFloorplanListingsForProperty(propertyId));
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }

    }

    @GetMapping("/{propertyId}/faqs")
    public ResponseEntity<List<Faq>> getFaqs(@PathVariable("propertyId") String propertyId){
        return ResponseEntity.ok(propertyService.getPropertyFaqs(propertyId));
    }
}
