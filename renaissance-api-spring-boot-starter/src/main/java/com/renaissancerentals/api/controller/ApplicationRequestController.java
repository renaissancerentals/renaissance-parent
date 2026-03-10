package com.renaissancerentals.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.api.service.ApplicationRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applicationRequest")
@RequiredArgsConstructor
public class ApplicationRequestController {

    private final ApplicationRequestService applicationRequestService;

    @PostMapping
    public ResponseEntity<Void> postApplicationRequest(@RequestBody @Valid ApplicationRequest applicationRequest){

        applicationRequestService.save(applicationRequest);

        return ResponseEntity.ok().build();
    }

}
