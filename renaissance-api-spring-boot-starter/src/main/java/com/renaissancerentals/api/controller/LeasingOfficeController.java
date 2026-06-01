package com.renaissancerentals.api.controller;

import com.renaissancerentals.api.domain.projection.LeasingOfficeDetails;
import com.renaissancerentals.api.service.LeasingOfficeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leasingOffices")
@RequiredArgsConstructor
public class LeasingOfficeController {

    private final LeasingOfficeService leasingOfficeService;

    @GetMapping
    public ResponseEntity<List<LeasingOfficeDetails>> getLeasingOffices() {
        return ResponseEntity.ok(leasingOfficeService.getAllLeasingOffices());
    }
}
