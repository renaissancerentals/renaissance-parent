package com.renaissancerentals.api.controller;

import com.renaissancerentals.api.domain.*;
import com.renaissancerentals.api.domain.projection.Projection;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.FloorplanRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/floorplans")
@RequiredArgsConstructor
public class FloorplanController {
    private final FloorplanRepository floorplanRepository;
    private final FloorplanService floorplanService;

    @GetMapping("/{floorplanId}")
    public ResponseEntity<?> get(
            @PathVariable("floorplanId") String floorplanId,
            @RequestParam(value = "projection") Projection projection) {
        switch (projection) {
            case ENRICHED -> {
                return ResponseEntity.ok(floorplanService.getFloorplan(floorplanId));
            }
            case DETAILS -> {
                return ResponseEntity.ok(floorplanRepository
                        .getFloorplanDetails(floorplanId)
                        .orElseThrow(() ->
                                new NotFoundException(String.format("Floorplan with id: %s not found.", floorplanId))));
            }
            case SPOTLIGHT -> {
                return ResponseEntity.ok(floorplanRepository
                        .getFloorplanSpotlight(floorplanId)
                        .orElseThrow(() ->
                                new NotFoundException(String.format("Floorplan with id: %s not found.", floorplanId))));
            }
            default -> throw new NotFoundException(String.format("Floorplan with id: %s not found.", floorplanId));
        }
    }

    @GetMapping("/byPropertyId/{propertyId}")
    public ResponseEntity<?> getFloorplansBy(
            @PathVariable("propertyId") String propertyId, @RequestParam(value = "projection") Projection projection) {
        if (Projection.DETAILS.equals(projection)) {
            return ResponseEntity.ok(floorplanRepository.getFloorplanDetailsForProperty(propertyId));
        } else {
            throw new ClientException(
                    ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }

    @GetMapping
    public ResponseEntity<?> getFloorplans(
            @RequestParam(value = "filterBy", required = false) String filterBy,
            @RequestParam(value = "projection") Projection projection) {
        switch (projection) {
            case SPOTLIGHT -> {
                if ("featured".equals(filterBy)) {
                    return ResponseEntity.ok(floorplanRepository.getFeaturedSpotlights());
                } else {
                    throw new ClientException(
                            ErrorMessage.builder().message("Unsupported param").build());
                }
            }
            case DETAILS -> {
                return ResponseEntity.ok(floorplanRepository.getActiveFloorplansDetails());
            }
            default -> throw new ClientException(
                    ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }

    @GetMapping("/{floorplanId}/similar")
    public ResponseEntity<List<SimilarFloorplan>> getSimilarFloorplans(
            @PathVariable("floorplanId") String floorplanId) {
        return ResponseEntity.ok(floorplanService.findSimilarFloorplans(floorplanId));
    }

    @GetMapping("/{floorplanId}/variations")
    public ResponseEntity<List<FloorplanVariation>> getFloorplanVariations(
            @PathVariable("floorplanId") String floorplanId) {
        return ResponseEntity.ok(floorplanService.findFloorplanVariations(floorplanId));
    }

    @GetMapping("/{floorplanId}/testimonials")
    public ResponseEntity<List<Testimonial>> getFloorplanTestimonials(@PathVariable("floorplanId") String floorplanId) {
        return ResponseEntity.ok(floorplanService.findFloorplanTestimonials(floorplanId));
    }

    @GetMapping("/{floorplanId}/webSpecials")
    public ResponseEntity<List<WebSpecial>> getFloorplanWebSpecials(@PathVariable("floorplanId") String floorplanId) {
        return ResponseEntity.ok(floorplanService.findFloorplanWebSpecials(floorplanId));
    }

    @GetMapping("/{floorplanId}/faqs")
    public ResponseEntity<List<Faq>> getFloorplanFaqs(@PathVariable("floorplanId") String floorplanId) {
        return ResponseEntity.ok(floorplanService.findFloorplanFaqs(floorplanId));
    }
}
