package com.renaissancerentals.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.api.messaging.ContactEventRequest;
import com.renaissancerentals.api.service.AnalyticsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @PostMapping("/contact-events")
    public ResponseEntity<Void> contactEvent(@RequestBody @Valid ContactEventRequest contactEvent){
        analyticsService.handleContactEvent(contactEvent);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
