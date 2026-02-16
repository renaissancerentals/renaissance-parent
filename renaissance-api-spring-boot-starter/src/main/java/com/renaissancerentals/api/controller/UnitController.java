package com.renaissancerentals.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.api.repository.UnitRepository;
import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {
    private final UnitRepository unitRepository;

    @GetMapping
    public ResponseEntity<List<?>> getAll(@RequestParam(value = "projection") String projection){
        if ("address".equals(projection)) {
            return ResponseEntity.ok(unitRepository.getAllAddresses());
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<?> get(@PathVariable("unitId") String unitId,
            @RequestParam(value = "projection") String projection){
        if ("utilities".equals(projection)) {
            return ResponseEntity.ok(unitRepository.getUnitUtilities(unitId));
        } else {
            throw new ClientException(ErrorMessage.builder().message("Unsupported Projection").build());
        }
    }
}
