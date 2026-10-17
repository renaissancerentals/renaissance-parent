package com.renaissancerentals.api.controller;

import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.FloorplanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Where a floorplan that was renamed lives now. The public sites ask this when a floorplan address is not found, to
 * answer the old address with a permanent redirect to the new one.
 */
@RestController
@RequestMapping("/api/floorplanRedirects")
@RequiredArgsConstructor
public class FloorplanRedirectController {

    private final FloorplanRepository floorplanRepository;

    @GetMapping("/{oldSlug}")
    public ResponseEntity<Redirect> get(@PathVariable("oldSlug") String oldSlug) {
        return floorplanRepository
                .findCurrentIdOfRenamed(oldSlug)
                .map(current -> ResponseEntity.ok(new Redirect(oldSlug, current)))
                .orElseThrow(() -> new NotFoundException(String.format("No renamed floorplan with id: %s", oldSlug)));
    }

    public record Redirect(String from, String to) {}
}
